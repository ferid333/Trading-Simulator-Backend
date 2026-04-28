package com.trading.app.service;

import com.trading.app.api.dto.RegisterRequest;
import com.trading.app.domain.UserAccount;
import com.trading.app.repository.UserAccountRepository;
import com.trading.app.service.exception.BadRequestException;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;
	private final PasswordEncoder passwordEncoder;

	public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
		this.userAccountRepository = userAccountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserAccount register(RegisterRequest request) {
		String username = request.username().trim();
		if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
			throw new BadRequestException("Username already exists");
		}
		UserAccount user = new UserAccount(username, passwordEncoder.encode(request.password()), Instant.now());
		return userAccountRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount user = userAccountRepository.findByUsernameIgnoreCase(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return new User(user.getUsername(), user.getPasswordHash(),
			List.of(new SimpleGrantedAuthority("ROLE_USER")));
	}
}
