package com.trading.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "trade_orders")
public class TradeOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", nullable = false)
	private SimulationSession session;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private OrderSide side;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "price_per_share", nullable = false, precision = 19, scale = 2)
	private BigDecimal pricePerShare;

	@Column(name = "total_value", nullable = false, precision = 19, scale = 2)
	private BigDecimal totalValue;

	@Column(name = "scenario_market_date")
	private LocalDate scenarioMarketDate;

	@Column(name = "executed_at", nullable = false, updatable = false)
	private Instant executedAt;

	protected TradeOrder() {
	}

	public TradeOrder(SimulationSession session, Stock stock, OrderSide side, Integer quantity, BigDecimal pricePerShare,
		BigDecimal totalValue, LocalDate scenarioMarketDate, Instant executedAt) {
		this.session = session;
		this.stock = stock;
		this.side = side;
		this.quantity = quantity;
		this.pricePerShare = pricePerShare;
		this.totalValue = totalValue;
		this.scenarioMarketDate = scenarioMarketDate;
		this.executedAt = executedAt;
	}

	public Long getId() {
		return id;
	}

	public SimulationSession getSession() {
		return session;
	}

	public Stock getStock() {
		return stock;
	}

	public OrderSide getSide() {
		return side;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public BigDecimal getPricePerShare() {
		return pricePerShare;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public LocalDate getScenarioMarketDate() {
		return scenarioMarketDate;
	}

	public Instant getExecutedAt() {
		return executedAt;
	}
}
