package com.trading.app.api.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AdvanceSessionResponse(
	UUID sessionId,
	String scenarioCode,
	LocalDate currentDate
) {
}
