package com.fitassist.backend.integration.config;

import com.fitassist.backend.auth.RateLimitingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@TestConfiguration
public class MockRateLimitingConfig {

	@Bean
	public RateLimitingFilter rateLimitingFilter() {
		return new RateLimitingFilter(null, null) {
			@Override
			protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
					@NotNull FilterChain filterChain) throws ServletException, IOException {
				filterChain.doFilter(request, response);
			}
		};
	}

}
