package com.trading.app.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlaceOrderRequest(
	@NotBlank String symbol,
	@Min(1) int quantity
) {
}
