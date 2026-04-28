package com.trading.app.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PortfolioResponse(
	String scenarioCode,
	LocalDate currentDate,
	BigDecimal startingBalance,
	BigDecimal cashBalance,
	BigDecimal holdingsValue,
	BigDecimal totalPortfolioValue,
	List<PortfolioHoldingResponse> holdings
) {
}
