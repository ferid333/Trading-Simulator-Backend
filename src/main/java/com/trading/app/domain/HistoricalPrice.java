package com.trading.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historical_prices")
public class HistoricalPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;

	@Column(name = "price_date", nullable = false)
	private LocalDate priceDate;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal openPrice;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal highPrice;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal lowPrice;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal closePrice;

	protected HistoricalPrice() {
	}

	public HistoricalPrice(Stock stock, LocalDate priceDate, BigDecimal openPrice, BigDecimal highPrice,
		BigDecimal lowPrice, BigDecimal closePrice) {
		this.stock = stock;
		this.priceDate = priceDate;
		this.openPrice = openPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.closePrice = closePrice;
	}

	public Long getId() {
		return id;
	}

	public Stock getStock() {
		return stock;
	}

	public LocalDate getPriceDate() {
		return priceDate;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public BigDecimal getHighPrice() {
		return highPrice;
	}

	public BigDecimal getLowPrice() {
		return lowPrice;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}
}
