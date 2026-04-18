package com.trading.app.api.dto;

import com.trading.app.domain.ScenarioType;
import java.time.LocalDate;

public record ScenarioResponse(
	String code,
	String name,
	ScenarioType type,
	LocalDate startDate,
	String description
) {
}
