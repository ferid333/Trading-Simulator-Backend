package com.trading.app.api;

import com.trading.app.api.dto.ScenarioResponse;
import com.trading.app.service.ScenarioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scenarios")
public class ScenarioController {

	private final ScenarioService scenarioService;

	public ScenarioController(ScenarioService scenarioService) {
		this.scenarioService = scenarioService;
	}

	@GetMapping
	public List<ScenarioResponse> getScenarios() {
		return scenarioService.getScenarios();
	}
}
