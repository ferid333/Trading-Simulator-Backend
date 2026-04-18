package com.trading.app.client;

import com.trading.app.client.dto.TwelveDataQuoteResponse;
import com.trading.app.client.dto.TwelveDataTimeSeriesResponse;
import com.trading.app.config.TwelveDataProperties;
import com.trading.app.service.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TwelveDataClient {

	private final RestClient restClient;
	private final TwelveDataProperties properties;

	public TwelveDataClient(RestClient restClient, TwelveDataProperties properties) {
		this.restClient = restClient;
		this.properties = properties;
	}

	public TwelveDataQuoteResponse getQuote(String symbol) {
		TwelveDataQuoteResponse response = restClient.get()
			.uri(properties.baseUrl() + "/quote?symbol={symbol}&apikey={apiKey}", symbol, properties.apiKey())
			.retrieve()
			.body(TwelveDataQuoteResponse.class);
		validateStatus(response == null ? null : response.status(), response == null ? "Empty response from quote API" : response.message());
		return response;
	}

	public TwelveDataTimeSeriesResponse getDailySeries(String symbol, String startDate, String endDate) {
		TwelveDataTimeSeriesResponse response = restClient.get()
			.uri(properties.baseUrl()
				+ "/time_series?symbol={symbol}&interval=1day&start_date={startDate}&end_date={endDate}&outputsize=100&apikey={apiKey}",
				symbol, startDate, endDate, properties.apiKey())
			.retrieve()
			.body(TwelveDataTimeSeriesResponse.class);
		validateStatus(response == null ? null : response.status(),
			response == null ? "Empty response from time series API" : response.message());
		return response;
	}

	private void validateStatus(String status, String message) {
		if (status != null && !"ok".equalsIgnoreCase(status)) {
			throw new BadRequestException(message == null ? "Twelve Data request failed" : message);
		}
	}
}
