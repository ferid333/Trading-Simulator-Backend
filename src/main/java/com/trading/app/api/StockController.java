package com.trading.app.api;

import com.trading.app.api.dto.StockResponse;
import com.trading.app.service.StockService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
public class StockController {

	private final StockService stockService;

	public StockController(StockService stockService) {
		this.stockService = stockService;
	}

	@GetMapping
	public List<StockResponse> getStocks() {
		return stockService.getStocks();
	}
}
