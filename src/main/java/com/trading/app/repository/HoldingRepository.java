package com.trading.app.repository;

import com.trading.app.domain.Holding;
import com.trading.app.domain.SimulationSession;
import com.trading.app.domain.Stock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

	List<Holding> findAllBySession(SimulationSession session);

	Optional<Holding> findBySessionAndStock(SimulationSession session, Stock stock);

	void deleteAllBySession(SimulationSession session);
}
