package com.trading.app.service;

import com.trading.app.api.dto.ScenarioResponse;
import com.trading.app.domain.Scenario;
import com.trading.app.repository.ScenarioRepository;
import com.trading.app.service.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScenarioService {

	private final ScenarioRepository scenarioRepository;

	public ScenarioService(ScenarioRepository scenarioRepository) {
		this.scenarioRepository = scenarioRepository;
	}

	public List<ScenarioResponse> getScenarios() {
		return scenarioRepository.findAll().stream()
			.map(this::toResponse)
			.toList();
	}

	public Scenario getByCode(String code) {
		return scenarioRepository.findByCodeIgnoreCase(code)
			.orElseThrow(() -> new NotFoundException("Scenario not found: " + code));
	}

	private ScenarioResponse toResponse(Scenario scenario) {
		return new ScenarioResponse(
			scenario.getCode(),
			scenario.getName(),
			scenario.getType(),
			scenario.getStartDate(),
			scenario.getDescription()
		);
	}
}
