package com.trading.app.service;

import com.trading.app.api.dto.StockResponse;
import com.trading.app.domain.Stock;
import com.trading.app.repository.StockRepository;
import com.trading.app.service.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	public List<StockResponse> getStocks() {
		return stockRepository.findAll().stream()
			.map(stock -> new StockResponse(stock.getSymbol(), stock.getName(), stock.getCurrency()))
			.toList();
	}

	public Stock getBySymbol(String symbol) {
		return stockRepository.findBySymbolIgnoreCase(symbol)
			.orElseThrow(() -> new NotFoundException("Stock not found: " + symbol));
	}
}
