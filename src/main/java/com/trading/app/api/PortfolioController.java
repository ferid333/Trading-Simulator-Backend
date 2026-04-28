package com.trading.app.api;

import com.trading.app.api.dto.AdvancePortfolioResponse;
import com.trading.app.api.dto.InitializePortfolioRequest;
import com.trading.app.api.dto.OrderResponse;
import com.trading.app.api.dto.PlaceOrderRequest;
import com.trading.app.api.dto.PortfolioResponse;
import com.trading.app.api.dto.PortfolioStateResponse;
import com.trading.app.api.dto.PriceResponse;
import com.trading.app.service.PortfolioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

	private final PortfolioService portfolioService;

	public PortfolioController(PortfolioService portfolioService) {
		this.portfolioService = portfolioService;
	}

	@PostMapping("/portfolio")
	@ResponseStatus(HttpStatus.CREATED)
	public PortfolioStateResponse initializePortfolio(@Valid @RequestBody InitializePortfolioRequest request,
		Authentication authentication) {
		return portfolioService.initializePortfolio(authentication.getName(), request);
	}

	@GetMapping("/portfolio")
	public PortfolioResponse getPortfolio(Authentication authentication) {
		return portfolioService.getPortfolio(authentication.getName());
	}

	@GetMapping("/prices")
	public List<PriceResponse> getPrices(Authentication authentication) {
		return portfolioService.getPrices(authentication.getName());
	}

	@GetMapping("/orders")
	public List<OrderResponse> getOrders(Authentication authentication) {
		return portfolioService.getOrders(authentication.getName());
	}

	@PostMapping("/orders/buy")
	public OrderResponse buy(@Valid @RequestBody PlaceOrderRequest request, Authentication authentication) {
		return portfolioService.buy(authentication.getName(), request);
	}

	@PostMapping("/orders/sell")
	public OrderResponse sell(@Valid @RequestBody PlaceOrderRequest request, Authentication authentication) {
		return portfolioService.sell(authentication.getName(), request);
	}

	@PostMapping("/portfolio/reset")
	public PortfolioStateResponse reset(Authentication authentication) {
		return portfolioService.reset(authentication.getName());
	}

	@PostMapping("/portfolio/advance")
	public AdvancePortfolioResponse advance(Authentication authentication) {
		return portfolioService.advance(authentication.getName());
	}
}
