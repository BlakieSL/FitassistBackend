package com.fitassist.backend.auth;

import com.fitassist.backend.service.declaration.ratelimiter.RedissonRateLimiterService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private final RedissonRateLimiterService rateLimitingService;

	private final RequestMatcher publicEndpointsMatcher;

	private final CookieService cookieService;

	public RateLimitingFilter(RedissonRateLimiterService rateLimitingService, RequestMatcher publicEndpointsMatcher,
			CookieService cookieService) {
		this.rateLimitingService = rateLimitingService;
		this.publicEndpointsMatcher = publicEndpointsMatcher;
		this.cookieService = cookieService;
	}

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull FilterChain filterChain) throws ServletException, IOException {

		if (publicEndpointsMatcher.matches(request)) {
			String id = getOrCreateId(request, response);
			String rateLimitKey = id + ":" + request.getRequestURI();

			if (!rateLimitingService.isAllowed(rateLimitKey)) {
				response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				return;
			}
		}
		else {
			String token = cookieService.getAccessTokenFromCookie(request).orElse(null);
			if (token == null) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				return;
			}

			UserInfo userInfo = parseToken(token);
			if (userInfo == null) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				return;
			}

			boolean isAdmin = userInfo.roles().contains("ROLE_ADMIN");
			if (!isAdmin && !rateLimitingService.isAllowed(userInfo.userId())) {
				response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String getOrCreateId(HttpServletRequest request, HttpServletResponse response) {
		return cookieService.getRateLimitCookie(request).orElseGet(() -> {
			String newValue = UUID.randomUUID().toString();
			cookieService.setRateLimitCookie(response, newValue);
			return newValue;
		});
	}

	private UserInfo parseToken(String token) {
		try {
			var claims = SignedJWT.parse(token).getJWTClaimsSet();
			return new UserInfo(claims.getIntegerClaim("userId"), claims.getStringListClaim("authorities"));
		}
		catch (Exception e) {
			return null;
		}
	}

	private record UserInfo(int userId, List<String> roles) {
	}

}
