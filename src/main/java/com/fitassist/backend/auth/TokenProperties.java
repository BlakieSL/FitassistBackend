package com.fitassist.backend.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "cookie")
@Component
@Getter
@Setter
public class TokenProperties {

	private TokenConfig accessToken;

	private TokenConfig refreshToken;

	private TokenConfig rateLimit;

	@Getter
	@Setter
	public static class TokenConfig {

		private String name;

		private int maxAge;

		public int getDurationMinutes() {
			return maxAge / 60;
		}

	}

}
