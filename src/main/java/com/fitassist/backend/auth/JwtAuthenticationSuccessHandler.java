package com.fitassist.backend.auth;

import com.fitassist.backend.service.implementation.user.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtService jwtService;

	private final UserServiceImpl userServiceImpl;

	private final CookieService cookieService;

	public JwtAuthenticationSuccessHandler(JwtService jwtService, UserServiceImpl userServiceImpl,
			CookieService cookieService) {
		this.jwtService = jwtService;
		this.userServiceImpl = userServiceImpl;
		this.cookieService = cookieService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		List<String> authorities = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		Integer userId = userServiceImpl.getUserIdByEmail(authentication.getName());
		String accessToken = jwtService.createAccessToken(authentication.getName(), userId, authorities);
		String refreshToken = jwtService.createRefreshToken(authentication.getName(), userId, authorities);

		cookieService.setAccessTokenCookie(response, accessToken);
		cookieService.setRefreshTokenCookie(response, refreshToken);
		response.setStatus(HttpStatus.OK.value());
	}

}
