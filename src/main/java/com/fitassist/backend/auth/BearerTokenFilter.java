package com.fitassist.backend.auth;

import com.fitassist.backend.exception.JwtAuthenticationException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public class BearerTokenFilter extends HttpFilter {

	private final JwtService jwtService;

	private final CookieService cookieService;

	private final TokenProperties tokenProperties;

	public BearerTokenFilter(JwtService jwtService, CookieService cookieService, TokenProperties tokenProperties) {
		this.jwtService = jwtService;
		this.cookieService = cookieService;
		this.tokenProperties = tokenProperties;
	}

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Optional<String> bearerToken = cookieService.getAccessTokenFromCookie(request);

		if (bearerToken.isEmpty()) {
			chain.doFilter(request, response);
			return;
		}

		try {
			SignedJWT signedJwt = SignedJWT.parse(bearerToken.get());
			validateJwt(signedJwt);
			setSecurityContext(signedJwt);
			chain.doFilter(request, response);
		}
		catch (JwtAuthenticationException | ParseException e) {
			throw new JwtAuthenticationException("Bearer token could not be parsed or is invalid");
		}
	}

	private void validateJwt(SignedJWT signedJwt) throws JwtAuthenticationException, ParseException {
		jwtService.verifySignature(signedJwt);
		jwtService.verifyExpirationTime(signedJwt);
		validateTokenType(signedJwt);
	}

	private void validateTokenType(SignedJWT signedJwt) throws ParseException {
		String tokenType = signedJwt.getJWTClaimsSet().getStringClaim("tokenType");
		if (!tokenProperties.getAccessToken().getName().equals(tokenType)) {
			throw new JwtAuthenticationException("Invalid token type for authorization");
		}
	}

	private void setSecurityContext(SignedJWT signedJwt) {
		Authentication authentication = jwtService.authentication(signedJwt);
		SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(authentication);
	}

}
