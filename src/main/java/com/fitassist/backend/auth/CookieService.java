package com.fitassist.backend.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Service
@ConfigurationProperties(prefix = "cookie")
@Getter
@Setter
public class CookieService {

	private TokenConfig accessToken;

	private TokenConfig refreshToken;

	private TokenConfig rateLimit;

	@Getter
	@Setter
	public static class TokenConfig {

		private String name;

		private int maxAge;

	}

	public void setAccessTokenCookie(HttpServletResponse response, String value) {
		Cookie cookie = createSecureCookie(accessToken.getName(), value, accessToken.getMaxAge());
		response.addCookie(cookie);
	}

	public Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
		return getCookieValue(request, accessToken.getName());
	}

	public void setRefreshTokenCookie(HttpServletResponse response, String value) {
		Cookie cookie = createSecureCookie(refreshToken.getName(), value, refreshToken.getMaxAge());
		response.addCookie(cookie);
	}

	public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
		return getCookieValue(request, refreshToken.getName());
	}

	public void setRateLimitCookie(HttpServletResponse response, String value) {
		Cookie cookie = createSecureCookie(rateLimit.getName(), value, rateLimit.getMaxAge());
		response.addCookie(cookie);
	}

	public Optional<String> getRateLimitCookie(HttpServletRequest request) {
		return getCookieValue(request, rateLimit.getName());
	}

	public void clearAuthCookies(HttpServletResponse response) {
		Cookie accessTokenCookie = createSecureCookie(accessToken.getName(), "", 0);
		Cookie refreshTokenCookie = createSecureCookie(refreshToken.getName(), "", 0);
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);
	}

	public String getAccessTokenCookieName() {
		return accessToken.getName();
	}

	public String getRefreshTokenCookieName() {
		return refreshToken.getName();
	}

	private Cookie createSecureCookie(String key, String value, int maxAge) {
		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		return cookie;
	}

	private Optional<String> getCookieValue(HttpServletRequest request, String key) {
		Cookie cookie = WebUtils.getCookie(request, key);
		return Optional.ofNullable(cookie).map(Cookie::getValue);
	}

}
