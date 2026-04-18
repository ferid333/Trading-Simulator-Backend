package com.trading.app.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TwelveDataTimeSeriesResponse(
	List<TwelveDataTimeSeriesValue> values,
	String status,
	String message
) {
}
