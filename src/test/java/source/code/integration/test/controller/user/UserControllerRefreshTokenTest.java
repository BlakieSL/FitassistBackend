package source.code.integration.test.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.auth.JwtService;
import source.code.dto.request.RefreshTokenRequestDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class UserControllerRefreshTokenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Value("${jws.sharedKey}")
    private String sharedKey;

    @WithAnonymousUser
    @Test
    @DisplayName("POST - /refresh-token - Should return new access token with valid refresh token")
     void refreshTokenSuccess() throws Exception {
        String validRefreshToken = jwtService.createAccessToken("username", 1, List.of("ROLE_USER"));

        var request = new RefreshTokenRequestDto(validRefreshToken);

        mockMvc.perform(post("/api/users/refresh-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").isNotEmpty()
                );
    }

    @WithAnonymousUser
    @Test
    @DisplayName("POST - /refresh-token - Should return 400 when invalid refresh token")
    void refreshTokenInvalid() throws Exception {
        String invalidToken = "thisIsNotAValidJWT";

        var request = new RefreshTokenRequestDto(invalidToken);

        mockMvc.perform(post("/api/users/refresh-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
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

        var request = new RefreshTokenRequestDto(expiredToken.serialize());

        mockMvc.perform(post("/api/users/refresh-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
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

        var request = new RefreshTokenRequestDto(tamperedToken.serialize());

        mockMvc.perform(post("/api/users/refresh-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
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

            var request = new RefreshTokenRequestDto(tokenWithMissingClaims.serialize());

            mockMvc.perform(post("/api/users/refresh-token")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpectAll(status().isBadRequest());

    }
}
