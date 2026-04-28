package com.trading.app.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PortfolioStateResponse(
	String scenarioCode,
	BigDecimal startingBalance,
	BigDecimal cashBalance,
	LocalDate currentDate
) {
}
