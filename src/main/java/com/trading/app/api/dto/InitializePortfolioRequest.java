package com.trading.app.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InitializePortfolioRequest(
	@NotBlank String scenarioCode,
	@NotNull @DecimalMin(value = "1.00") BigDecimal startingBalance
) {
}
