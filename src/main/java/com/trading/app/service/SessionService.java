package com.trading.app.service;

import com.trading.app.api.dto.AdvanceSessionResponse;
import com.trading.app.api.dto.CreateSessionRequest;
import com.trading.app.api.dto.OrderResponse;
import com.trading.app.api.dto.PlaceOrderRequest;
import com.trading.app.api.dto.PortfolioHoldingResponse;
import com.trading.app.api.dto.PortfolioResponse;
import com.trading.app.api.dto.PriceResponse;
import com.trading.app.api.dto.SessionResponse;
import com.trading.app.domain.Holding;
import com.trading.app.domain.OrderSide;
import com.trading.app.domain.Scenario;
import com.trading.app.domain.ScenarioType;
import com.trading.app.domain.SimulationSession;
import com.trading.app.domain.Stock;
import com.trading.app.domain.TradeOrder;
import com.trading.app.repository.HoldingRepository;
import com.trading.app.repository.SimulationSessionRepository;
import com.trading.app.repository.StockRepository;
import com.trading.app.repository.TradeOrderRepository;
import com.trading.app.service.exception.BadRequestException;
import com.trading.app.service.exception.NotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

	private final ScenarioService scenarioService;
	private final StockService stockService;
	private final PriceService priceService;
	private final SimulationSessionRepository sessionRepository;
	private final HoldingRepository holdingRepository;
	private final TradeOrderRepository tradeOrderRepository;
	private final StockRepository stockRepository;

	public SessionService(ScenarioService scenarioService, StockService stockService, PriceService priceService,
		SimulationSessionRepository sessionRepository, HoldingRepository holdingRepository,
		TradeOrderRepository tradeOrderRepository, StockRepository stockRepository) {
		this.scenarioService = scenarioService;
		this.stockService = stockService;
		this.priceService = priceService;
		this.sessionRepository = sessionRepository;
		this.holdingRepository = holdingRepository;
		this.tradeOrderRepository = tradeOrderRepository;
		this.stockRepository = stockRepository;
	}

	@Transactional
	public SessionResponse createSession(CreateSessionRequest request) {
		Scenario scenario = scenarioService.getByCode(request.scenarioCode());
		BigDecimal startingBalance = scale(request.startingBalance());
		LocalDate currentDate = scenario.getType() == ScenarioType.HISTORICAL ? scenario.getStartDate() : null;
		SimulationSession session = new SimulationSession(
			UUID.randomUUID(),
			scenario,
			startingBalance,
			startingBalance,
			currentDate,
			Instant.now()
		);
		SimulationSession saved = sessionRepository.save(session);
		return new SessionResponse(saved.getSessionId(), scenario.getCode(), saved.getStartingBalance(), saved.getCashBalance(),
			saved.getCurrentDate());
	}

	@Transactional
	public PortfolioResponse getPortfolio(UUID sessionId) {
		SimulationSession session = getSession(sessionId);
		List<Holding> holdings = holdingRepository.findAllBySession(session);
		Map<String, PriceResponse> prices = new HashMap<>();
		BigDecimal holdingsValue = BigDecimal.ZERO;

		List<PortfolioHoldingResponse> items = holdings.stream().map(holding -> {
			PriceResponse price = prices.computeIfAbsent(holding.getStock().getSymbol(),
				key -> priceService.getSessionPrice(session, holding.getStock()));
			BigDecimal marketValue = price.price().multiply(BigDecimal.valueOf(holding.getQuantity()));
			BigDecimal costBasis = holding.getAverageBuyPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
			return new PortfolioHoldingResponse(
				holding.getStock().getSymbol(),
				holding.getStock().getName(),
				holding.getQuantity(),
				holding.getAverageBuyPrice(),
				price.price(),
				scale(marketValue),
				scale(marketValue.subtract(costBasis))
			);
		}).toList();

		for (PortfolioHoldingResponse item : items) {
			holdingsValue = holdingsValue.add(item.marketValue());
		}

		return new PortfolioResponse(
			session.getSessionId(),
			session.getScenario().getCode(),
			session.getCurrentDate(),
			session.getStartingBalance(),
			session.getCashBalance(),
			scale(holdingsValue),
			scale(session.getCashBalance().add(holdingsValue)),
			items
		);
	}

	@Transactional
	public List<PriceResponse> getPrices(UUID sessionId) {
		SimulationSession session = getSession(sessionId);
		return priceService.getSessionPrices(session, stockRepository.findAll());
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrders(UUID sessionId) {
		SimulationSession session = getSession(sessionId);
		return tradeOrderRepository.findAllBySessionOrderByExecutedAtDesc(session).stream()
			.map(this::toOrderResponse)
			.toList();
	}

	@Transactional
	public OrderResponse buy(UUID sessionId, PlaceOrderRequest request) {
		return placeOrder(sessionId, request, OrderSide.BUY);
	}

	@Transactional
	public OrderResponse sell(UUID sessionId, PlaceOrderRequest request) {
		return placeOrder(sessionId, request, OrderSide.SELL);
	}

	@Transactional
	public SessionResponse reset(UUID sessionId) {
		SimulationSession session = getSession(sessionId);
		holdingRepository.deleteAllBySession(session);
		tradeOrderRepository.deleteAllBySession(session);
		session.setCashBalance(session.getStartingBalance());
		if (session.getScenario().getType() == ScenarioType.HISTORICAL) {
			session.setCurrentDate(session.getScenario().getStartDate());
		}
		SimulationSession saved = sessionRepository.save(session);
		return new SessionResponse(saved.getSessionId(), saved.getScenario().getCode(), saved.getStartingBalance(),
			saved.getCashBalance(), saved.getCurrentDate());
	}

	@Transactional
	public AdvanceSessionResponse advance(UUID sessionId) {
		SimulationSession session = getSession(sessionId);
		if (session.getScenario().getType() != ScenarioType.HISTORICAL) {
			throw new BadRequestException("Only historical sessions can be advanced");
		}
		Stock referenceStock = stockRepository.findAll().stream().findFirst()
			.orElseThrow(() -> new NotFoundException("No stocks configured"));
		LocalDate nextDate = priceService.findNextTradingDay(referenceStock, session.getCurrentDate());
		session.setCurrentDate(nextDate);
		sessionRepository.save(session);
		return new AdvanceSessionResponse(session.getSessionId(), session.getScenario().getCode(), session.getCurrentDate());
	}

	public SimulationSession getSession(UUID sessionId) {
		return sessionRepository.findBySessionId(sessionId)
			.orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
	}

	private OrderResponse placeOrder(UUID sessionId, PlaceOrderRequest request, OrderSide side) {
		SimulationSession session = getSession(sessionId);
		Stock stock = stockService.getBySymbol(request.symbol());
		PriceResponse marketPrice = priceService.getSessionPrice(session, stock);
		BigDecimal totalValue = scale(marketPrice.price().multiply(BigDecimal.valueOf(request.quantity())));
		Holding holding = holdingRepository.findBySessionAndStock(session, stock).orElse(null);

		if (side == OrderSide.BUY) {
			if (session.getCashBalance().compareTo(totalValue) < 0) {
				throw new BadRequestException("Insufficient cash balance");
			}
			session.setCashBalance(scale(session.getCashBalance().subtract(totalValue)));
			if (holding == null) {
				holding = new Holding(session, stock, request.quantity(), marketPrice.price());
			} else {
				BigDecimal existingCost = holding.getAverageBuyPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
				BigDecimal newCost = marketPrice.price().multiply(BigDecimal.valueOf(request.quantity()));
				int newQuantity = holding.getQuantity() + request.quantity();
				BigDecimal averagePrice = existingCost.add(newCost).divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);
				holding.setQuantity(newQuantity);
				holding.setAverageBuyPrice(scale(averagePrice));
			}
			holdingRepository.save(holding);
		} else {
			if (holding == null || holding.getQuantity() < request.quantity()) {
				throw new BadRequestException("Not enough shares to sell");
			}
			session.setCashBalance(scale(session.getCashBalance().add(totalValue)));
			int remainingQuantity = holding.getQuantity() - request.quantity();
			if (remainingQuantity == 0) {
				holdingRepository.delete(holding);
			} else {
				holding.setQuantity(remainingQuantity);
				holdingRepository.save(holding);
			}
		}

		sessionRepository.save(session);
		TradeOrder order = tradeOrderRepository.save(new TradeOrder(
			session,
			stock,
			side,
			request.quantity(),
			marketPrice.price(),
			totalValue,
			marketPrice.marketDate(),
			Instant.now()
		));
		return toOrderResponse(order);
	}

	private OrderResponse toOrderResponse(TradeOrder order) {
		return new OrderResponse(
			order.getId(),
			order.getStock().getSymbol(),
			order.getSide(),
			order.getQuantity(),
			order.getPricePerShare(),
			order.getTotalValue(),
			order.getScenarioMarketDate(),
			order.getExecutedAt()
		);
	}

	private BigDecimal scale(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}
}
