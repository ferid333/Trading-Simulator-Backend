package com.trading.app.repository;

import com.trading.app.domain.SimulationSession;
import com.trading.app.domain.TradeOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {

	List<TradeOrder> findAllBySessionOrderByExecutedAtDesc(SimulationSession session);

	void deleteAllBySession(SimulationSession session);
}
