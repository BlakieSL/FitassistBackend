package source.code.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import source.code.service.declaration.rateLimiter.RedissonRateLimiterService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final String RATE_LIMIT_COOKIE_NAME = "RateLimit-Id";
    private final RedissonRateLimiterService rateLimitingService;
    private final RequestMatcher requestMatcher;

    public RateLimitingFilter(RedissonRateLimiterService rateLimitingService, RequestMatcher requestMatcher) {
        this.rateLimitingService = rateLimitingService;
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicEndpoint(request)) {
            String identifier = getOrCreateRateLimitCookie(request, response);
            if (!handleRateLimitingForNonAuth(identifier, path, response)) {
                return;
            }
        } else {
            if (!handleAuthRequestRateLimiting(request, response)) {
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        return requestMatcher != null && requestMatcher.matches(request);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(authHeader -> authHeader.startsWith("Bearer"))
                .map(authHeader -> authHeader.substring("Bearer".length()).trim());
    }

    private Optional<UserInfo> extractUserIdAndRoles(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            int userId = claimsSet.getIntegerClaim("userId");
            List<String> roles = claimsSet.getStringListClaim("authorities");

            return Optional.of(new UserInfo(userId, roles));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean handleRateLimiting(UserInfo userInfo, HttpServletResponse response) throws IOException {
        if (userInfo.roles.contains("ROLE_ADMIN")) {
            return true;
        }

        if (rateLimitingService.isAllowed(userInfo.userId)) {
            return true;
        } else {
            return writeErrorResponse(response, 429, "Too many requests");
        }
    }

    private boolean handleAuthRequestRateLimiting(HttpServletRequest request, HttpServletResponse response) {
        return extractToken(request)
                .flatMap(this::extractUserIdAndRoles)
                .map(userInfo -> safelyHandleRateLimiting(userInfo, response))
                .orElseGet(() -> safelyHandleInvalidToken(response));
    }

    private boolean safelyHandleRateLimiting(UserInfo userInfo, HttpServletResponse response) {
        try {
            return handleRateLimiting(userInfo, response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to handle rate limiting", e);
        }
    }

    private boolean safelyHandleInvalidToken(HttpServletResponse response) {
        try {
            return handleInvalidToken(response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to handle invalid token", e);
        }
    }

    private boolean handleInvalidToken(HttpServletResponse response) throws IOException {
        return writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
    }

    private boolean writeErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException
    {
        response.setStatus(statusCode);
        response.getWriter().write(message);
        return false;
    }

    private String getOrCreateRateLimitCookie(HttpServletRequest request, HttpServletResponse response) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> RATE_LIMIT_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseGet(() -> createAndSetNewRateLimitCookie(response));
    }

    private String createAndSetNewRateLimitCookie(HttpServletResponse response) {
        String newIdentifier = UUID.randomUUID().toString();
        Cookie rateLimitCookie = new Cookie(RATE_LIMIT_COOKIE_NAME, newIdentifier);
        rateLimitCookie.setPath("/");
        rateLimitCookie.setHttpOnly(true);
        rateLimitCookie.setMaxAge(5 * 60);
        response.addCookie(rateLimitCookie);
        return newIdentifier;
    }

    private boolean handleRateLimitingForNonAuth(String identifier, String path, HttpServletResponse response)
            throws IOException
    {
        String rateLimitKey = identifier + ":" + path;
        if (rateLimitingService.isAllowed(rateLimitKey)) {
            return true;
        } else {
            return writeErrorResponse(response, 429, "Too many requests - please try again later");
        }
    }

    private record UserInfo(int userId, List<String> roles) {
    }
}
