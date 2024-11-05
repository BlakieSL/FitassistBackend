package source.code.auth;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import source.code.exception.JwtAuthenticationException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public class BearerTokenFilter extends HttpFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private final AuthenticationFailureHandler failureHandler;
    private final JwtService jwtService;

    public BearerTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        failureHandler = new SimpleUrlAuthenticationFailureHandler();
    }

    @Override
    protected void doFilter(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        Optional<String> bearerToken = getBearerToken(request);

        if (bearerToken.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            SignedJWT signedJwt = SignedJWT.parse(bearerToken.get());
            validateJwt(signedJwt);
            setSecurityContext(signedJwt);
            chain.doFilter(request, response);
        } catch (JwtAuthenticationException | ParseException e) {
            failureHandler.onAuthenticationFailure(
                    request,
                    response,
                    new JwtAuthenticationException("Bearer token could not be parsed or is invalid")
            );
        }
    }

    private Optional<String> getBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authorizationHeader.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private void validateJwt(SignedJWT signedJwt) throws JwtAuthenticationException, ParseException {
        jwtService.verifySignature(signedJwt);
        jwtService.verifyExpirationTime(signedJwt);
        validateTokenType(signedJwt);
    }

    private void validateTokenType(SignedJWT signedJwt) throws ParseException {
        String tokenType = signedJwt.getJWTClaimsSet().getStringClaim("tokenType");
        if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
            throw new JwtAuthenticationException("Invalid token type for authorization");
        }
    }

    private void setSecurityContext(SignedJWT signedJwt) {
        Authentication authentication = jwtService.authentication(signedJwt);
        SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(authentication);
    }
}
