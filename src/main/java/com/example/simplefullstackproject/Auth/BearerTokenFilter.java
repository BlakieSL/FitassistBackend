package com.example.simplefullstackproject.Auth;

import com.example.simplefullstackproject.Exceptions.JwtAuthenticationException;
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

import java.io.IOException;
import java.text.ParseException;

public class BearerTokenFilter extends HttpFilter {
    private final AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private final JwtService jwtService;

    public BearerTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            chain.doFilter(request, response);
        } else {
            String compactJwt = authorizationHeader.substring("Bearer ".length());
            SignedJWT signedJwt;
            try {
                signedJwt = SignedJWT.parse(compactJwt);
                verifyJwt(signedJwt);
                String tokenType = signedJwt.getJWTClaimsSet().getStringClaim("tokenType");
                if (!"ACCESS".equals(tokenType)) {
                    throw new JwtAuthenticationException("Invalid token type for authorization");
                }
                setSecurityContext(signedJwt);
                chain.doFilter(request, response);
            } catch (JwtAuthenticationException e) {
                failureHandler.onAuthenticationFailure(request, response, e);
            } catch (ParseException e) {
                JwtAuthenticationException authException = new JwtAuthenticationException("Bearer token could not be parsed");
                failureHandler.onAuthenticationFailure(request, response, authException);
            }
        }
    }

    private void setSecurityContext(SignedJWT signedJwt) {
        Authentication authentication = jwtService.authentication(signedJwt);
        SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(authentication);
    }

    private void verifyJwt(SignedJWT signedJwt) {
        jwtService.verifySignature(signedJwt);
        jwtService.verifyExpirationTime(signedJwt);
    }
}
