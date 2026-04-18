package com.trading.app.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
	UUID sessionId,
	String scenarioCode,
	LocalDate currentDate,
	BigDecimal startingBalance,
	BigDecimal cashBalance,
	BigDecimal holdingsValue,
	BigDecimal totalPortfolioValue,
	List<PortfolioHoldingResponse> holdings
) {
}
