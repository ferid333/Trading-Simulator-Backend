package com.trading.app.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceResponse(
	String symbol,
	BigDecimal price,
	LocalDate marketDate,
	String scenarioCode,
	String source
) {
}
