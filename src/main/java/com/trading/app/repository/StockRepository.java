package com.trading.app.repository;

import com.trading.app.domain.Stock;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

	Optional<Stock> findBySymbolIgnoreCase(String symbol);
}
