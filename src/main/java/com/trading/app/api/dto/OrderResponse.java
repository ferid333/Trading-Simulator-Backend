package com.trading.app.api.dto;

import com.trading.app.domain.OrderSide;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record OrderResponse(
	Long orderId,
	String symbol,
	OrderSide side,
	Integer quantity,
	BigDecimal pricePerShare,
	BigDecimal totalValue,
	LocalDate scenarioMarketDate,
	Instant executedAt
) {
}
