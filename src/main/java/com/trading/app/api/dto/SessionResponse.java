package com.trading.app.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SessionResponse(
	UUID sessionId,
	String scenarioCode,
	BigDecimal startingBalance,
	BigDecimal cashBalance,
	LocalDate currentDate
) {
}
