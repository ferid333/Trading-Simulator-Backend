package com.trading.app.api;

import com.trading.app.api.dto.AdvanceSessionResponse;
import com.trading.app.api.dto.CreateSessionRequest;
import com.trading.app.api.dto.OrderResponse;
import com.trading.app.api.dto.PlaceOrderRequest;
import com.trading.app.api.dto.PortfolioResponse;
import com.trading.app.api.dto.PriceResponse;
import com.trading.app.api.dto.SessionResponse;
import com.trading.app.service.SessionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {

	private final SessionService sessionService;

	public SessionController(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SessionResponse createSession(@Valid @RequestBody CreateSessionRequest request) {
		return sessionService.createSession(request);
	}

	@GetMapping("/{sessionId}/portfolio")
	public PortfolioResponse getPortfolio(@PathVariable UUID sessionId) {
		return sessionService.getPortfolio(sessionId);
	}

	@GetMapping("/{sessionId}/prices")
	public List<PriceResponse> getPrices(@PathVariable UUID sessionId) {
		return sessionService.getPrices(sessionId);
	}

	@GetMapping("/{sessionId}/orders")
	public List<OrderResponse> getOrders(@PathVariable UUID sessionId) {
		return sessionService.getOrders(sessionId);
	}

	@PostMapping("/{sessionId}/orders/buy")
	public OrderResponse buy(@PathVariable UUID sessionId, @Valid @RequestBody PlaceOrderRequest request) {
		return sessionService.buy(sessionId, request);
	}

	@PostMapping("/{sessionId}/orders/sell")
	public OrderResponse sell(@PathVariable UUID sessionId, @Valid @RequestBody PlaceOrderRequest request) {
		return sessionService.sell(sessionId, request);
	}

	@PostMapping("/{sessionId}/reset")
	public SessionResponse reset(@PathVariable UUID sessionId) {
		return sessionService.reset(sessionId);
	}

	@PostMapping("/{sessionId}/advance")
	public AdvanceSessionResponse advance(@PathVariable UUID sessionId) {
		return sessionService.advance(sessionId);
	}
}
