package com.trading.app.domain;

import com.trading.app.api.dto.PriceResponse;
import com.trading.app.service.PriceService;
import com.trading.app.service.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "scenarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "scenario_kind", discriminatorType = DiscriminatorType.STRING)
public class Scenario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String code;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ScenarioType type;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(nullable = false, length = 255)
	private String description;

	protected Scenario() {
	}

	public Scenario(String code, String name, ScenarioType type, LocalDate startDate, String description) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.startDate = startDate;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public ScenarioType getType() {
		return type;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getInitialMarketDate() {
		return null;
	}

	public LocalDate getResetMarketDate() {
		return getInitialMarketDate();
	}

	public PriceResponse getPrice(Stock stock, LocalDate currentDate, PriceService priceService) {
		return priceService.getLivePrice(this, stock);
	}

	public LocalDate advanceMarketDate(Stock referenceStock, LocalDate currentDate, PriceService priceService) {
		throw new BadRequestException("Only historical portfolios can be advanced");
	}
}
