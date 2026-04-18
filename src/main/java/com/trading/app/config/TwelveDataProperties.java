package com.trading.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "twelvedata")
public record TwelveDataProperties(
	String apiKey,
	String baseUrl
) {
}
