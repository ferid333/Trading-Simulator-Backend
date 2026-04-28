package com.trading.app.bootstrap;

import com.trading.app.domain.HistoricalScenario;
import com.trading.app.domain.LiveScenario;
import com.trading.app.domain.Scenario;
import com.trading.app.domain.Stock;
import com.trading.app.repository.ScenarioRepository;
import com.trading.app.repository.StockRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final ScenarioRepository scenarioRepository;
	private final StockRepository stockRepository;

	public DataInitializer(ScenarioRepository scenarioRepository, StockRepository stockRepository) {
		this.scenarioRepository = scenarioRepository;
		this.stockRepository = stockRepository;
	}

	@Override
	public void run(String... args) {
		seedScenarios();
		seedStocks();
	}

	private void seedScenarios() {
		if (scenarioRepository.count() > 0) {
			return;
		}
		scenarioRepository.saveAll(List.of(
			new LiveScenario("LIVE", "Live Market", "Trade against current market prices."),
			new HistoricalScenario("COVID", "COVID Crash", LocalDate.of(2020, 3, 2),
				"Starts at the 2020 COVID market crash."),
			new HistoricalScenario("FINANCIAL_CRISIS", "Financial Crisis", LocalDate.of(2008, 9, 15),
				"Starts at the 2008 global financial crisis.")
		));
	}

	private void seedStocks() {
		if (stockRepository.count() > 0) {
			return;
		}
		stockRepository.saveAll(List.of(
			new Stock("AAPL", "Apple Inc.", "USD"),
			new Stock("MSFT", "Microsoft Corporation", "USD"),
			new Stock("AMZN", "Amazon.com, Inc.", "USD"),
			new Stock("GOOGL", "Alphabet Inc.", "USD"),
			new Stock("TSLA", "Tesla, Inc.", "USD")
		));
	}
}
