package com.fitassist.backend.auth;

import com.fitassist.backend.exception.InvalidRefreshTokenException;
import com.fitassist.backend.exception.JwtAuthenticationException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

	private final JWSAlgorithm algorithm = JWSAlgorithm.HS256;

	private final JWSSigner signer;

	private final JWSVerifier verifier;

	private final TokenProperties tokenProperties;

	public JwtService(@Value("${jws.sharedKey}") String sharedKey, TokenProperties tokenProperties) throws Exception {
		byte[] sharedKeyBytes = sharedKey.getBytes();
		this.signer = new MACSigner(sharedKeyBytes);
		this.verifier = new MACVerifier(sharedKeyBytes);
		this.tokenProperties = tokenProperties;
	}

	public String createAccessToken(String username, Integer userId, List<String> authorities) {
		TokenProperties.TokenConfig config = tokenProperties.getAccessToken();
		return createSignedJWT(username, userId, authorities, config.getDurationMinutes(), config.getName());
	}

	public String createRefreshToken(String username, Integer userId, List<String> authorities) {
		TokenProperties.TokenConfig config = tokenProperties.getRefreshToken();
		return createSignedJWT(username, userId, authorities, config.getDurationMinutes(), config.getName());
	}

	public String refreshAccessToken(String refreshToken) {
		try {
			SignedJWT signedJWT = (SignedJWT) JWTParser.parse(refreshToken);
			verifySignature(signedJWT);
			verifyExpirationTime(signedJWT);

			JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

			String subject = claims.getSubject();
			if (subject == null) {
				throw new InvalidRefreshTokenException("Refresh token has no subject");
			}

			Integer userId = claims.getIntegerClaim("userId");
			if (userId == null) {
				throw new InvalidRefreshTokenException("Refresh token has no user ID");
			}

			List<String> authorities = claims.getStringListClaim("authorities");
			if (authorities == null) {
				throw new InvalidRefreshTokenException("Refresh token has no authorities");
			}

			return createAccessToken(subject, userId, authorities);
		}
		catch (ParseException e) {
			throw new InvalidRefreshTokenException("Invalid refresh token");
		}
	}

	public String createSignedJWT(String username, Integer userId, List<String> authorities, long durationInMinutes,
			String tokenType) {
		try {
			JWTClaimsSet claimSet = buildClaims(username, userId, authorities, durationInMinutes, tokenType);
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claimSet);
			signedJWT.sign(signer);

			return signedJWT.serialize();
		}
		catch (JOSEException e) {
			throw new JwtAuthenticationException("Failed to sign JWT" + e);
		}
	}

	public void verifySignature(SignedJWT signedJWT) {
		try {
			if (!signedJWT.verify(verifier))
				throw new JwtAuthenticationException("JWT not verified - token: " + signedJWT.serialize());
		}
		catch (JOSEException ex) {
			throw new JwtAuthenticationException("JWT not verified - token: " + signedJWT.serialize());
		}
	}

	public void verifyExpirationTime(SignedJWT signedJWT) {
		try {
			Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
			if (expiration.before(Date.from(Instant.now()))) {
				throw new JwtAuthenticationException("JWT expired");
			}
		}
		catch (ParseException e) {
			throw new JwtAuthenticationException("JWT expiration time is invalid");
		}
	}

	public Authentication authentication(SignedJWT signedJWT) {
		try {
			String subject = signedJWT.getJWTClaimsSet().getSubject();
			Integer userId = signedJWT.getJWTClaimsSet().getIntegerClaim("userId");
			List<SimpleGrantedAuthority> authorities = getAuthorities(signedJWT);
			return new CustomAuthenticationToken(subject, userId, null, authorities);
		}
		catch (ParseException e) {
			throw new JwtAuthenticationException("Missing or invalid claims in JWT");
		}
	}

	private JWTClaimsSet buildClaims(String username, Integer userId, List<String> authorities, long durationMinutes,
			String tokenType) {
		return new JWTClaimsSet.Builder().subject(username)
			.issueTime(currentDate())
			.expirationTime(expirationDate(durationMinutes))
			.claim("userId", userId)
			.claim("authorities", authorities)
			.claim("tokenType", tokenType)
			.build();
	}

	private Date currentDate() {
		return Date.from(Instant.now());
	}

	private Date expirationDate(long minutesFromNow) {
		return Date.from(Instant.now().plusSeconds(minutesFromNow * 60));
	}

	private List<SimpleGrantedAuthority> getAuthorities(SignedJWT signedJWT) throws ParseException {
		return signedJWT.getJWTClaimsSet()
			.getStringListClaim("authorities")
			.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

}
