package com.trading.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "simulation_sessions")
public class SimulationSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserAccount owner;

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

	public SimulationSession(UserAccount owner, Scenario scenario, BigDecimal startingBalance, BigDecimal cashBalance,
		LocalDate currentDate, Instant createdAt) {
		this.owner = owner;
		this.scenario = scenario;
		this.startingBalance = startingBalance;
		this.cashBalance = cashBalance;
		this.currentDate = currentDate;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public UserAccount getOwner() {
		return owner;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public BigDecimal getStartingBalance() {
		return startingBalance;
	}

	public void setStartingBalance(BigDecimal startingBalance) {
		this.startingBalance = startingBalance;
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
