package source.code.auth;

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
import source.code.exception.JwtAuthenticationException;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private static final long ACCESS_TOKEN_DURATION_MINUTES = 15;
    private static final long REFRESH_TOKEN_DURATION_MINUTES = 60 * 24 * 7;
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final JWSAlgorithm algorithm = JWSAlgorithm.HS256;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JwtService(@Value("${jws.sharedKey}") String sharedKey)
            throws Exception
    {
        byte[] sharedKeyBytes = sharedKey.getBytes();
        this.signer = new MACSigner(sharedKeyBytes);
        this.verifier = new MACVerifier(sharedKeyBytes);
    }

    public String createAccessToken(String username, Integer userId, List<String> authorities) {
        return createSignedJWT(
                username,
                userId,
                authorities,
                15,
                ACCESS_TOKEN_TYPE);
    }

    public String createRefreshToken(String username, Integer userId, List<String> authorities) {
        return createSignedJWT(
                username,
                userId,
                authorities,
                60 * 24 * 7,
                REFRESH_TOKEN_TYPE);
    }

    public String createSignedJWT(
            String username, Integer userId, List<String> authorities,
            long durationInMinutes, String tokenType)
    {
        try {
            JWTClaimsSet claimSet = buildClaims(
                    username,
                    userId,
                    authorities,
                    durationInMinutes,
                    tokenType
            );
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claimSet);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("Failed to sign JWT" + e);
        }
    }

    public void verifySignature(SignedJWT signedJWT) {
        try {
            if (!signedJWT.verify(verifier))
                throw new JwtAuthenticationException("JWT not verified - token: " + signedJWT.serialize());
        } catch (JOSEException ex) {
            throw new JwtAuthenticationException("JWT not verified - token: " + signedJWT.serialize());
        }
    }

    public void verifyExpirationTime(SignedJWT signedJWT) {
        try {
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration.before(Date.from(Instant.now()))) {
                throw new JwtAuthenticationException("JWT expired");
            }
        } catch (ParseException e) {
            throw new JwtAuthenticationException("JWT expiration time is invalid");
        }
    }

    public Authentication authentication(SignedJWT signedJWT) {
        try {
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            Integer userId = signedJWT.getJWTClaimsSet().getIntegerClaim("userId");
            List<SimpleGrantedAuthority> authorities = getAuthorities(signedJWT);
            return new CustomAuthenticationToken(subject, userId, null, authorities);
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Missing or invalid claims in JWT");
        }
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            SignedJWT signedJWT = (SignedJWT) JWTParser.parse(refreshToken);
            verifySignature(signedJWT);
            verifyExpirationTime(signedJWT);
            return createAccessToken(
                    signedJWT.getJWTClaimsSet().getSubject(),
                    signedJWT.getJWTClaimsSet().getIntegerClaim("userId"),
                    signedJWT.getJWTClaimsSet().getStringListClaim("authorities")
            );
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Invalid refresh token");
        }
    }

    private JWTClaimsSet buildClaims(
            String username, Integer userId, List<String> authorities,
            long durationMinutes, String tokenType
    ) {
        return new JWTClaimsSet.Builder()
                .subject(username)
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
        return signedJWT.getJWTClaimsSet().getStringListClaim("authorities").stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}