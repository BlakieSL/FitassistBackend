package com.fitassist.backend.integration.test.controller.user;

import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.auth.TokenProperties;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class UserControllerRefreshTokenTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private TokenProperties tokenProperties;

	@Value("${jws.sharedKey}")
	private String sharedKey;

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /refresh-token - Should return new access token cookie with valid refresh token")
	void refreshTokenSuccess() throws Exception {
		String validRefreshToken = jwtService.createRefreshToken("username", 1, List.of("ROLE_USER"));

		mockMvc
			.perform(post("/api/users/refresh-token")
				.cookie(new Cookie(tokenProperties.getRefreshToken().getName(), validRefreshToken)))
			.andExpectAll(status().isOk(), cookie().exists(tokenProperties.getAccessToken().getName()));
	}

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /refresh-token - Should return 400 when invalid refresh token")
	void refreshTokenInvalid() throws Exception {
		String invalidToken = "thisIsNotAValidJWT";

		mockMvc
			.perform(post("/api/users/refresh-token")
				.cookie(new Cookie(tokenProperties.getRefreshToken().getName(), invalidToken)))
			.andExpectAll(status().isBadRequest());
	}

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /refresh-token - Should return 400 When token is expired")
	void refreshTokenExpired() throws Exception {
		String validToken = jwtService.createRefreshToken("testuser", 1, List.of("ROLE_USER"));

		SignedJWT signedJWT = SignedJWT.parse(validToken);
		JWTClaimsSet expiredClaimsSet = new JWTClaimsSet.Builder(signedJWT.getJWTClaimsSet())
			.expirationTime(Date.from(Instant.now().minusSeconds(60)))
			.build();

		SignedJWT expiredToken = new SignedJWT(signedJWT.getHeader(), expiredClaimsSet);
		expiredToken.sign(new MACSigner(sharedKey.getBytes()));

		mockMvc
			.perform(post("/api/users/refresh-token")
				.cookie(new Cookie(tokenProperties.getRefreshToken().getName(), expiredToken.serialize())))
			.andExpectAll(status().isUnauthorized());
	}

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /refresh-token - Should return 400 when invalid signature")
	void refreshTokenInvalidSignature() throws Exception {
		String validToken = jwtService.createRefreshToken("testuser", 1, List.of("ROLE_USER"));

		SignedJWT signedJWT = SignedJWT.parse(validToken);

		JWTClaimsSet tamperedClaimsSet = new JWTClaimsSet.Builder(signedJWT.getJWTClaimsSet())
			.claim("tamperedClaim", "tamperedValue")
			.build();

		SignedJWT tamperedToken = new SignedJWT(signedJWT.getHeader(), tamperedClaimsSet);
		tamperedToken.sign(new MACSigner("INVALID_SHARED_KEY_FOR_TESTING_PURPOSE".getBytes()));

		mockMvc
			.perform(post("/api/users/refresh-token")
				.cookie(new Cookie(tokenProperties.getRefreshToken().getName(), tamperedToken.serialize())))
			.andExpectAll(status().isUnauthorized());
	}

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /refresh-token - Should return 400 when missing claims")
	void refreshTokenMissingClaims() throws Exception {

		JWTClaimsSet missingClaimsSet = new JWTClaimsSet.Builder()
			.expirationTime(Date.from(Instant.now().plusSeconds(60)))
			.build();

		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).build();
		SignedJWT tokenWithMissingClaims = new SignedJWT(header, missingClaimsSet);
		tokenWithMissingClaims.sign(new MACSigner(sharedKey.getBytes()));

		mockMvc
			.perform(post("/api/users/refresh-token")
				.cookie(new Cookie(tokenProperties.getRefreshToken().getName(), tokenWithMissingClaims.serialize())))
			.andExpectAll(status().isBadRequest());
	}

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /refresh-token - Should return 400 when no refresh token cookie")
	void refreshTokenMissingCookie() throws Exception {
		mockMvc.perform(post("/api/users/refresh-token")).andExpectAll(status().isBadRequest());
	}

	@WithAnonymousUser
	@Test
	@DisplayName("POST - /logout - Should clear auth cookies")
	void logoutSuccess() throws Exception {
		mockMvc.perform(post("/api/users/logout"))
			.andExpectAll(status().isOk(), cookie().exists(tokenProperties.getAccessToken().getName()),
					cookie().exists(tokenProperties.getRefreshToken().getName()),
					cookie().maxAge(tokenProperties.getAccessToken().getName(), 0),
					cookie().maxAge(tokenProperties.getRefreshToken().getName(), 0));
	}

}
