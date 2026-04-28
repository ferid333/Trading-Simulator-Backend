package com.trading.app.service;

import com.trading.app.api.dto.AdvancePortfolioResponse;
import com.trading.app.api.dto.InitializePortfolioRequest;
import com.trading.app.api.dto.OrderResponse;
import com.trading.app.api.dto.PlaceOrderRequest;
import com.trading.app.api.dto.PortfolioHoldingResponse;
import com.trading.app.api.dto.PortfolioResponse;
import com.trading.app.api.dto.PortfolioStateResponse;
import com.trading.app.api.dto.PriceResponse;
import com.trading.app.domain.Holding;
import com.trading.app.domain.OrderSide;
import com.trading.app.domain.Scenario;
import com.trading.app.domain.SimulationSession;
import com.trading.app.domain.Stock;
import com.trading.app.domain.TradeOrder;
import com.trading.app.domain.UserAccount;
import com.trading.app.repository.HoldingRepository;
import com.trading.app.repository.SimulationSessionRepository;
import com.trading.app.repository.StockRepository;
import com.trading.app.repository.TradeOrderRepository;
import com.trading.app.repository.UserAccountRepository;
import com.trading.app.service.exception.BadRequestException;
import com.trading.app.service.exception.NotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {

	private final ScenarioService scenarioService;
	private final StockService stockService;
	private final PriceService priceService;
	private final SimulationSessionRepository sessionRepository;
	private final HoldingRepository holdingRepository;
	private final TradeOrderRepository tradeOrderRepository;
	private final StockRepository stockRepository;
	private final UserAccountRepository userAccountRepository;

	public PortfolioService(ScenarioService scenarioService, StockService stockService, PriceService priceService,
		SimulationSessionRepository sessionRepository, HoldingRepository holdingRepository,
		TradeOrderRepository tradeOrderRepository, StockRepository stockRepository,
		UserAccountRepository userAccountRepository) {
		this.scenarioService = scenarioService;
		this.stockService = stockService;
		this.priceService = priceService;
		this.sessionRepository = sessionRepository;
		this.holdingRepository = holdingRepository;
		this.tradeOrderRepository = tradeOrderRepository;
		this.stockRepository = stockRepository;
		this.userAccountRepository = userAccountRepository;
	}

	@Transactional
	public PortfolioStateResponse initializePortfolio(String username, InitializePortfolioRequest request) {
		UserAccount owner = getUser(username);
		Scenario scenario = scenarioService.getByCode(request.scenarioCode());
		BigDecimal startingBalance = scale(request.startingBalance());
		LocalDate currentDate = scenario.getInitialMarketDate();

		SimulationSession portfolio = sessionRepository.findByOwnerUsernameIgnoreCase(username)
			.orElseGet(() -> new SimulationSession(owner, scenario, startingBalance, startingBalance, currentDate, Instant.now()));

		if (portfolio.getId() != null) {
			holdingRepository.deleteAllBySession(portfolio);
			tradeOrderRepository.deleteAllBySession(portfolio);
			portfolio.setScenario(scenario);
			portfolio.setStartingBalance(startingBalance);
			portfolio.setCashBalance(startingBalance);
			portfolio.setCurrentDate(currentDate);
		}

		SimulationSession saved = sessionRepository.save(portfolio);
		return toPortfolioStateResponse(saved);
	}

	@Transactional
	public PortfolioResponse getPortfolio(String username) {
		SimulationSession session = getRequiredPortfolio(username);
		List<Holding> holdings = holdingRepository.findAllBySession(session);
		Map<String, PriceResponse> prices = new HashMap<>();
		BigDecimal holdingsValue = BigDecimal.ZERO;

		List<PortfolioHoldingResponse> items = holdings.stream().map(holding -> {
			PriceResponse price = prices.computeIfAbsent(holding.getStock().getSymbol(),
				key -> session.getCurrentDate() == null
					? priceService.getPrice(session.getScenario(), holding.getStock())
					: priceService.getPrice(session.getScenario(), holding.getStock(), session.getCurrentDate()));
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
	public List<PriceResponse> getPrices(String username) {
		SimulationSession session = getRequiredPortfolio(username);
		return stockRepository.findAll().stream()
			.map(stock -> session.getCurrentDate() == null
				? priceService.getPrice(session.getScenario(), stock)
				: priceService.getPrice(session.getScenario(), stock, session.getCurrentDate()))
			.toList();
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrders(String username) {
		SimulationSession session = getRequiredPortfolio(username);
		return tradeOrderRepository.findAllBySessionOrderByExecutedAtDesc(session).stream()
			.map(this::toOrderResponse)
			.toList();
	}

	@Transactional
	public OrderResponse buy(String username, PlaceOrderRequest request) {
		return placeOrder(username, request, OrderSide.BUY);
	}

	@Transactional
	public OrderResponse sell(String username, PlaceOrderRequest request) {
		return placeOrder(username, request, OrderSide.SELL);
	}

	@Transactional
	public PortfolioStateResponse reset(String username) {
		SimulationSession session = getRequiredPortfolio(username);
		holdingRepository.deleteAllBySession(session);
		tradeOrderRepository.deleteAllBySession(session);
		session.setCashBalance(session.getStartingBalance());
		session.setCurrentDate(session.getScenario().getResetMarketDate());
		SimulationSession saved = sessionRepository.save(session);
		return toPortfolioStateResponse(saved);
	}

	@Transactional
	public AdvancePortfolioResponse advance(String username) {
		SimulationSession session = getRequiredPortfolio(username);
		Stock referenceStock = stockRepository.findAll().stream().findFirst()
			.orElseThrow(() -> new NotFoundException("No stocks configured"));
		LocalDate nextDate = session.getScenario().advanceMarketDate(referenceStock, session.getCurrentDate(), priceService);
		session.setCurrentDate(nextDate);
		sessionRepository.save(session);
		return new AdvancePortfolioResponse(session.getScenario().getCode(), session.getCurrentDate());
	}

	private SimulationSession getRequiredPortfolio(String username) {
		return sessionRepository.findByOwnerUsernameIgnoreCase(username)
			.orElseThrow(() -> new NotFoundException("Portfolio not initialized for user: " + username));
	}

	private UserAccount getUser(String username) {
		return userAccountRepository.findByUsernameIgnoreCase(username)
			.orElseThrow(() -> new NotFoundException("User not found: " + username));
	}

	private OrderResponse placeOrder(String username, PlaceOrderRequest request, OrderSide side) {
		SimulationSession session = getRequiredPortfolio(username);
		Stock stock = stockService.getBySymbol(request.symbol());
		PriceResponse marketPrice = priceService.getPrice(session, stock);
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

	private PortfolioStateResponse toPortfolioStateResponse(SimulationSession session) {
		return new PortfolioStateResponse(
			session.getScenario().getCode(),
			session.getStartingBalance(),
			session.getCashBalance(),
			session.getCurrentDate()
		);
	}

	private BigDecimal scale(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}
}
