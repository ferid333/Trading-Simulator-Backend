package com.trading.app.api.dto;

public record StockResponse(
	String symbol,
	String name,
	String currency
) {
}
