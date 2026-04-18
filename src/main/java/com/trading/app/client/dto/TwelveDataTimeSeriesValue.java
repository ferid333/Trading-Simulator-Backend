package com.trading.app.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TwelveDataTimeSeriesValue(
	String datetime,
	String open,
	String high,
	String low,
	String close
) {
}
