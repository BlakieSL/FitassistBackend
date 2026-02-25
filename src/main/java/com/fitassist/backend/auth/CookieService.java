package com.fitassist.backend.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Service
public class CookieService {

	private final TokenProperties tokenProperties;

	@Value("${app.csrf.cookie-secure}")
	private boolean cookieSecure;

	public CookieService(TokenProperties tokenProperties) {
		this.tokenProperties = tokenProperties;
	}

	public void setAccessTokenCookie(HttpServletResponse response, String value) {
		TokenProperties.TokenConfig config = tokenProperties.getAccessToken();
		Cookie cookie = createSecureCookie(config.getName(), value, config.getMaxAge());
		response.addCookie(cookie);
	}

	public Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
		return getCookieValue(request, tokenProperties.getAccessToken().getName());
	}

	public void setRefreshTokenCookie(HttpServletResponse response, String value) {
		TokenProperties.TokenConfig config = tokenProperties.getRefreshToken();
		Cookie cookie = createSecureCookie(config.getName(), value, config.getMaxAge());
		response.addCookie(cookie);
	}

	public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
		return getCookieValue(request, tokenProperties.getRefreshToken().getName());
	}

	public void setRateLimitCookie(HttpServletResponse response, String value) {
		TokenProperties.TokenConfig config = tokenProperties.getRateLimit();
		Cookie cookie = createSecureCookie(config.getName(), value, config.getMaxAge());
		response.addCookie(cookie);
	}

	public Optional<String> getRateLimitCookie(HttpServletRequest request) {
		return getCookieValue(request, tokenProperties.getRateLimit().getName());
	}

	public void clearAuthCookies(HttpServletResponse response) {
		Cookie accessTokenCookie = createSecureCookie(tokenProperties.getAccessToken().getName(), "", 0);
		Cookie refreshTokenCookie = createSecureCookie(tokenProperties.getRefreshToken().getName(), "", 0);
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);
	}

	private Cookie createSecureCookie(String key, String value, int maxAge) {
		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(cookieSecure);
		if (cookieSecure) {
			cookie.setAttribute("SameSite", "None");
		}
		else {
			cookie.setAttribute("SameSite", "Lax");
		}
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		return cookie;
	}

	private Optional<String> getCookieValue(HttpServletRequest request, String key) {
		Cookie cookie = WebUtils.getCookie(request, key);
		return Optional.ofNullable(cookie).map(Cookie::getValue);
	}

}
