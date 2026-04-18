package com.trading.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stocks")
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String symbol;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 10)
	private String currency;

	protected Stock() {
	}

	public Stock(String symbol, String name, String currency) {
		this.symbol = symbol;
		this.name = name;
		this.currency = currency;
	}

	public Long getId() {
		return id;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public String getCurrency() {
		return currency;
	}
}
