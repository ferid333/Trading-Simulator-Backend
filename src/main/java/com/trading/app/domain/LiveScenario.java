package com.trading.app.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LIVE")
public class LiveScenario extends Scenario {

	protected LiveScenario() {
	}

	public LiveScenario(String code, String name, String description) {
		super(code, name, ScenarioType.LIVE, null, description);
	}
}
