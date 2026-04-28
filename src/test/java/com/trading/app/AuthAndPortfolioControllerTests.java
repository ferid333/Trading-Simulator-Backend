package com.trading.app;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAndPortfolioControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void protectedEndpointRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/portfolio"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void registerAndInitializePortfolioWorks() throws Exception {
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "alice",
					  "password": "password123"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.username").value("alice"));

		mockMvc.perform(post("/portfolio")
				.with(httpBasic("alice", "password123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "scenarioCode": "COVID",
					  "startingBalance": 10000
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.scenarioCode").value("COVID"))
			.andExpect(jsonPath("$.startingBalance").value(10000.00))
			.andExpect(jsonPath("$.cashBalance").value(10000.00));

		mockMvc.perform(get("/portfolio").with(httpBasic("alice", "password123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.scenarioCode").value("COVID"))
			.andExpect(jsonPath("$.totalPortfolioValue").value(10000.00));
	}
}
