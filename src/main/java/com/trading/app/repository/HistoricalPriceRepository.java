package com.trading.app.repository;

import com.trading.app.domain.HistoricalPrice;
import com.trading.app.domain.Stock;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, Long> {

	Optional<HistoricalPrice> findByStockAndPriceDate(Stock stock, LocalDate priceDate);
}
