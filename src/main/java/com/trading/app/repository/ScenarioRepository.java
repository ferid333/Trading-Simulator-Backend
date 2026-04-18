package com.trading.app.repository;

import com.trading.app.domain.Scenario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

	Optional<Scenario> findByCodeIgnoreCase(String code);
}
