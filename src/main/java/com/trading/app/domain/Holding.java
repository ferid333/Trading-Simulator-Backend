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

@Entity
@Table(name = "holdings")
public class Holding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", nullable = false)
	private SimulationSession session;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "average_buy_price", nullable = false, precision = 19, scale = 2)
	private BigDecimal averageBuyPrice;

	protected Holding() {
	}

	public Holding(SimulationSession session, Stock stock, Integer quantity, BigDecimal averageBuyPrice) {
		this.session = session;
		this.stock = stock;
		this.quantity = quantity;
		this.averageBuyPrice = averageBuyPrice;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAverageBuyPrice() {
		return averageBuyPrice;
	}

	public void setAverageBuyPrice(BigDecimal averageBuyPrice) {
		this.averageBuyPrice = averageBuyPrice;
	}
}
