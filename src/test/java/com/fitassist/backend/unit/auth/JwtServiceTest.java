package com.fitassist.backend.unit.auth;

import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.auth.TokenProperties;
import com.fitassist.backend.exception.InvalidRefreshTokenException;
import com.fitassist.backend.exception.JwtAuthenticationException;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

	@Mock
	private TokenProperties tokenProperties;

	private JwtService jwtService;

	private String username;

	private Integer userId;

	private List<String> authorities;

	private static final String ACCESS_TOKEN_NAME = "accessToken";

	private static final String REFRESH_TOKEN_NAME = "refreshToken";

	@BeforeEach
	void setUp() throws Exception {
		String sharedKey = "thisIsASecretKeyForTestingPurposesThatIsLongEnough";

		TokenProperties.TokenConfig accessConfig = new TokenProperties.TokenConfig();
		accessConfig.setName(ACCESS_TOKEN_NAME);
		accessConfig.setMaxAge(900);

		TokenProperties.TokenConfig refreshConfig = new TokenProperties.TokenConfig();
		refreshConfig.setName(REFRESH_TOKEN_NAME);
		refreshConfig.setMaxAge(604800);

		when(tokenProperties.getAccessToken()).thenReturn(accessConfig);
		when(tokenProperties.getRefreshToken()).thenReturn(refreshConfig);

		jwtService = new JwtService(sharedKey, tokenProperties);
		username = "testuser";
		userId = 1;
		authorities = List.of("ROLE_USER");
	}

	@Test
	void createAccessToken_shouldReturnValidToken() throws ParseException {
		String token = jwtService.createAccessToken(username, userId, authorities);

		assertNotNull(token);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
		assertEquals(username, signedJWT.getJWTClaimsSet().getSubject());
		assertEquals(userId, signedJWT.getJWTClaimsSet().getIntegerClaim("userId"));
		assertEquals(authorities, signedJWT.getJWTClaimsSet().getStringListClaim("authorities"));
		assertEquals(ACCESS_TOKEN_NAME, signedJWT.getJWTClaimsSet().getStringClaim("tokenType"));
	}

	@Test
	void createRefreshToken_shouldReturnValidToken() throws ParseException {
		String token = jwtService.createRefreshToken(username, userId, authorities);

		assertNotNull(token);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
		assertEquals(username, signedJWT.getJWTClaimsSet().getSubject());
		assertEquals(userId, signedJWT.getJWTClaimsSet().getIntegerClaim("userId"));
		assertEquals(authorities, signedJWT.getJWTClaimsSet().getStringListClaim("authorities"));
		assertEquals(REFRESH_TOKEN_NAME, signedJWT.getJWTClaimsSet().getStringClaim("tokenType"));
	}

	@Test
	void verifySignature_shouldNotThrowForValidToken() throws ParseException {
		String token = jwtService.createAccessToken(username, userId, authorities);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);

		assertDoesNotThrow(() -> jwtService.verifySignature(signedJWT));
	}

	@Test
	void verifySignature_shouldThrowForInvalidSignature() throws Exception {
		String differentKey = "differentSecretKeyForTestingThatIsAlsoLongEnough";
		JwtService differentJwtService = new JwtService(differentKey, tokenProperties);
		String token = differentJwtService.createAccessToken(username, userId, authorities);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);

		assertThrows(JwtAuthenticationException.class, () -> jwtService.verifySignature(signedJWT));
	}

	@Test
	void verifyExpirationTime_shouldNotThrowForValidToken() throws ParseException {
		String token = jwtService.createAccessToken(username, userId, authorities);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);

		assertDoesNotThrow(() -> jwtService.verifyExpirationTime(signedJWT));
	}

	@Test
	void verifyExpirationTime_shouldThrowForExpiredToken() throws ParseException {
		String token = jwtService.createSignedJWT(username, userId, authorities, -1, ACCESS_TOKEN_NAME);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);

		assertThrows(JwtAuthenticationException.class, () -> jwtService.verifyExpirationTime(signedJWT));
	}

	@Test
	void authentication_shouldReturnValidAuthentication() throws ParseException {
		String token = jwtService.createAccessToken(username, userId, authorities);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);

		Authentication authentication = jwtService.authentication(signedJWT);

		assertNotNull(authentication);
		assertEquals(username, authentication.getName());
		assertTrue(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
	}

	@Test
	void refreshAccessToken_shouldReturnNewAccessToken() throws ParseException {
		String refreshToken = jwtService.createRefreshToken(username, userId, authorities);

		String newAccessToken = jwtService.refreshAccessToken(refreshToken);

		assertNotNull(newAccessToken);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(newAccessToken);
		assertEquals(username, signedJWT.getJWTClaimsSet().getSubject());
		assertEquals(ACCESS_TOKEN_NAME, signedJWT.getJWTClaimsSet().getStringClaim("tokenType"));
	}

	@Test
	void refreshAccessToken_shouldThrowForExpiredRefreshToken() {
		String expiredToken = jwtService.createSignedJWT(username, userId, authorities, -1, REFRESH_TOKEN_NAME);

		assertThrows(JwtAuthenticationException.class, () -> jwtService.refreshAccessToken(expiredToken));
	}

	@Test
	void refreshAccessToken_shouldThrowForInvalidToken() {
		String invalidToken = "invalid.token.here";

		assertThrows(InvalidRefreshTokenException.class, () -> jwtService.refreshAccessToken(invalidToken));
	}

	@Test
	void createAccessToken_shouldContainExpirationTime() throws ParseException {
		String token = jwtService.createAccessToken(username, userId, authorities);
		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);

		assertNotNull(signedJWT.getJWTClaimsSet().getExpirationTime());
		assertNotNull(signedJWT.getJWTClaimsSet().getIssueTime());
	}

	@Test
	void createRefreshToken_shouldHaveLongerExpiration() throws ParseException {
		String accessToken = jwtService.createAccessToken(username, userId, authorities);
		String refreshToken = jwtService.createRefreshToken(username, userId, authorities);

		SignedJWT accessJWT = (SignedJWT) JWTParser.parse(accessToken);
		SignedJWT refreshJWT = (SignedJWT) JWTParser.parse(refreshToken);

		assertTrue(refreshJWT.getJWTClaimsSet()
			.getExpirationTime()
			.after(accessJWT.getJWTClaimsSet().getExpirationTime()));
	}

	@Test
	void createAccessToken_withMultipleAuthorities_shouldIncludeAll() throws ParseException {
		List<String> multipleAuthorities = List.of("ROLE_USER", "ROLE_ADMIN");
		String token = jwtService.createAccessToken(username, userId, multipleAuthorities);

		SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
		List<String> claimedAuthorities = signedJWT.getJWTClaimsSet().getStringListClaim("authorities");

		assertEquals(2, claimedAuthorities.size());
		assertTrue(claimedAuthorities.contains("ROLE_USER"));
		assertTrue(claimedAuthorities.contains("ROLE_ADMIN"));
	}

}
