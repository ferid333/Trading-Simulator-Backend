package com.trading.app.api.dto;

import java.time.LocalDate;

public record AdvancePortfolioResponse(
	String scenarioCode,
	LocalDate currentDate
) {
}
