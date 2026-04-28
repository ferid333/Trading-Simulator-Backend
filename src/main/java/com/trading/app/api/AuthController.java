package com.trading.app.api;

import com.trading.app.api.dto.AuthResponse;
import com.trading.app.api.dto.RegisterRequest;
import com.trading.app.domain.UserAccount;
import com.trading.app.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserAccountService userAccountService;

	public AuthController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		UserAccount user = userAccountService.register(request);
		return new AuthResponse(user.getUsername());
	}
}
