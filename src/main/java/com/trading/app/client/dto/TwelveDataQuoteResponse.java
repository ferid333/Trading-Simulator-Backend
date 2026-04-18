package com.trading.app.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TwelveDataQuoteResponse(
	String symbol,
	String close,
	@JsonProperty("previous_close") String previousClose,
	String datetime,
	String status,
	String message
) {
}
