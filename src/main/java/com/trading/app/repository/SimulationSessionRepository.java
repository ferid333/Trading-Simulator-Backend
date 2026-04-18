package com.trading.app.repository;

import com.trading.app.domain.SimulationSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationSessionRepository extends JpaRepository<SimulationSession, Long> {

	Optional<SimulationSession> findBySessionId(UUID sessionId);
}
