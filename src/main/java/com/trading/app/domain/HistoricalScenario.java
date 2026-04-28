package com.trading.app.domain;

import com.trading.app.api.dto.PriceResponse;
import com.trading.app.service.PriceService;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("HISTORICAL")
public class HistoricalScenario extends Scenario {

	protected HistoricalScenario() {
	}

	public HistoricalScenario(String code, String name, LocalDate startDate, String description) {
		super(code, name, ScenarioType.HISTORICAL, startDate, description);
	}

	@Override
	public LocalDate getInitialMarketDate() {
		return getStartDate();
	}

	@Override
	public PriceResponse getPrice(Stock stock, LocalDate currentDate, PriceService priceService) {
		return priceService.getHistoricalSessionPrice(this, stock, currentDate);
	}

	@Override
	public LocalDate advanceMarketDate(Stock referenceStock, LocalDate currentDate, PriceService priceService) {
		return priceService.findNextTradingDay(referenceStock, currentDate);
	}
}
