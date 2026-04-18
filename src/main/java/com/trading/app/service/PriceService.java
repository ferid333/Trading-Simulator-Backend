package com.trading.app.service;

import com.trading.app.api.dto.PriceResponse;
import com.trading.app.client.TwelveDataClient;
import com.trading.app.client.dto.TwelveDataQuoteResponse;
import com.trading.app.client.dto.TwelveDataTimeSeriesResponse;
import com.trading.app.client.dto.TwelveDataTimeSeriesValue;
import com.trading.app.domain.HistoricalPrice;
import com.trading.app.domain.Scenario;
import com.trading.app.domain.ScenarioType;
import com.trading.app.domain.SimulationSession;
import com.trading.app.domain.Stock;
import com.trading.app.repository.HistoricalPriceRepository;
import com.trading.app.service.exception.BadRequestException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PriceService {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final TwelveDataClient twelveDataClient;
	private final HistoricalPriceRepository historicalPriceRepository;

	public PriceService(TwelveDataClient twelveDataClient, HistoricalPriceRepository historicalPriceRepository) {
		this.twelveDataClient = twelveDataClient;
		this.historicalPriceRepository = historicalPriceRepository;
	}

	public PriceResponse getSessionPrice(SimulationSession session, Stock stock) {
		if (session.getScenario().getType() == ScenarioType.LIVE) {
			return getLivePrice(session.getScenario(), stock);
		}
		return getHistoricalSessionPrice(session.getScenario(), stock, session.getCurrentDate());
	}

	public List<PriceResponse> getSessionPrices(SimulationSession session, List<Stock> stocks) {
		return stocks.stream().map(stock -> getSessionPrice(session, stock)).toList();
	}

	public PriceResponse getLivePrice(Scenario scenario, Stock stock) {
		TwelveDataQuoteResponse response = twelveDataClient.getQuote(stock.getSymbol());
		BigDecimal price = parseDecimal(response.close());
		LocalDate marketDate = parseQuoteDate(response.datetime());
		return new PriceResponse(stock.getSymbol(), scale(price), marketDate, scenario.getCode(), "TWELVE_DATA_QUOTE");
	}

	@Transactional
	public PriceResponse getHistoricalSessionPrice(Scenario scenario, Stock stock, LocalDate marketDate) {
		HistoricalPrice cached = historicalPriceRepository.findByStockAndPriceDate(stock, marketDate).orElse(null);
		if (cached == null) {
			cached = fetchAndStoreNearestTradingDay(stock, marketDate);
		}
		return new PriceResponse(stock.getSymbol(), cached.getClosePrice(), cached.getPriceDate(), scenario.getCode(), "MYSQL_CACHE");
	}

	@Transactional
	public LocalDate findNextTradingDay(Stock stock, LocalDate afterDate) {
		for (int i = 1; i <= 10; i++) {
			LocalDate candidate = afterDate.plusDays(i);
			if (historicalPriceRepository.findByStockAndPriceDate(stock, candidate).isPresent()) {
				return candidate;
			}
			try {
				return fetchAndStoreNearestTradingDay(stock, candidate).getPriceDate();
			} catch (BadRequestException ignored) {
				// Continue until a valid trading day is found.
			}
		}
		throw new BadRequestException("No next trading day found after " + afterDate);
	}

	private HistoricalPrice fetchAndStoreNearestTradingDay(Stock stock, LocalDate marketDate) {
		LocalDate endDate = marketDate.plusDays(10);
		TwelveDataTimeSeriesResponse response = twelveDataClient.getDailySeries(
			stock.getSymbol(),
			marketDate.toString(),
			endDate.toString()
		);
		List<TwelveDataTimeSeriesValue> values = response.values();
		if (values == null || values.isEmpty()) {
			throw new BadRequestException("No historical market data for " + stock.getSymbol() + " from " + marketDate + " to " + endDate);
		}
		TwelveDataTimeSeriesValue point = values.stream()
			.map(value -> new HistoricalPoint(value, parseSeriesDate(value.datetime())))
			.filter(pointValue -> !pointValue.priceDate().isBefore(marketDate))
			.min(Comparator.comparing(HistoricalPoint::priceDate))
			.map(HistoricalPoint::value)
			.orElseThrow(() -> new BadRequestException("No historical market data for " + stock.getSymbol() + " from " + marketDate + " to " + endDate));
		LocalDate priceDate = parseSeriesDate(point.datetime());
		HistoricalPrice entity = new HistoricalPrice(
			stock,
			priceDate,
			scale(parseDecimal(point.open())),
			scale(parseDecimal(point.high())),
			scale(parseDecimal(point.low())),
			scale(parseDecimal(point.close()))
		);
		return historicalPriceRepository.findByStockAndPriceDate(stock, priceDate)
			.orElseGet(() -> historicalPriceRepository.save(entity));
	}

	private BigDecimal parseDecimal(String value) {
		if (value == null || value.isBlank()) {
			throw new BadRequestException("Missing price from data provider");
		}
		return new BigDecimal(value);
	}

	private LocalDate parseSeriesDate(String value) {
		try {
			return LocalDate.parse(value);
		} catch (DateTimeParseException ignored) {
			try {
				return LocalDateTime.parse(value, DATE_TIME_FORMATTER).toLocalDate();
			} catch (DateTimeParseException ex) {
				throw new BadRequestException("Invalid historical date: " + value);
			}
		}
	}

	private LocalDate parseQuoteDate(String value) {
		if (value == null || value.isBlank()) {
			return LocalDate.now();
		}
		try {
			return LocalDate.parse(value);
		} catch (DateTimeParseException ignored) {
			try {
				return LocalDateTime.parse(value, DATE_TIME_FORMATTER).toLocalDate();
			} catch (DateTimeParseException ignoredAgain) {
				try {
					return OffsetDateTime.parse(value).toLocalDate();
				} catch (DateTimeParseException ex) {
					return LocalDate.now();
				}
			}
		}
	}

	private BigDecimal scale(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}

	private record HistoricalPoint(TwelveDataTimeSeriesValue value, LocalDate priceDate) {
	}
}
