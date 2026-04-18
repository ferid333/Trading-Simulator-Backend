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
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "simulation_sessions")
public class SimulationSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private UUID sessionId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "scenario_id", nullable = false)
	private Scenario scenario;

	@Column(name = "starting_balance", nullable = false, precision = 19, scale = 2)
	private BigDecimal startingBalance;

	@Column(name = "cash_balance", nullable = false, precision = 19, scale = 2)
	private BigDecimal cashBalance;

	@Column(name = "market_date")
	private LocalDate currentDate;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected SimulationSession() {
	}

	public SimulationSession(UUID sessionId, Scenario scenario, BigDecimal startingBalance, BigDecimal cashBalance,
		LocalDate currentDate, Instant createdAt) {
		this.sessionId = sessionId;
		this.scenario = scenario;
		this.startingBalance = startingBalance;
		this.cashBalance = cashBalance;
		this.currentDate = currentDate;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public UUID getSessionId() {
		return sessionId;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public BigDecimal getStartingBalance() {
		return startingBalance;
	}

	public BigDecimal getCashBalance() {
		return cashBalance;
	}

	public void setCashBalance(BigDecimal cashBalance) {
		this.cashBalance = cashBalance;
	}

	public LocalDate getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(LocalDate currentDate) {
		this.currentDate = currentDate;
	}
}
