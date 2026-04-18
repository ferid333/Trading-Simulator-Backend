package com.trading.app.api.dto;

import java.math.BigDecimal;

public record PortfolioHoldingResponse(
	String symbol,
	String name,
	Integer quantity,
	BigDecimal averageBuyPrice,
	BigDecimal currentPrice,
	BigDecimal marketValue,
	BigDecimal unrealizedProfitLoss
) {
}
