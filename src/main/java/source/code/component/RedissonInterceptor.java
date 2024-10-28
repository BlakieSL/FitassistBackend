package source.code.component;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import source.code.service.Declaration.RateLimiter.RedissonRateLimiterService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class RedissonInterceptor implements HandlerInterceptor {
  private final RedissonRateLimiterService rateLimitingService;
  private static final List<String> NON_AUTH_ENDPOINTS = Arrays.asList("/api/users/login", "/api/users/register");

  public RedissonInterceptor(RedissonRateLimiterService rateLimitingService) {
    this.rateLimitingService = rateLimitingService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String path = request.getRequestURI();
    if (isNonAuthEndpoint(path)) {
      return true;
    }

    return extractToken(request)
            .flatMap(this::extractUserId)
            .map(userId -> handleRateLimiting(userId, response))
            .orElseGet(() -> handleInvalidToken(response));
  }

  private boolean isNonAuthEndpoint(String path) {
    return NON_AUTH_ENDPOINTS.contains(path);
  }

  private Optional<String> extractToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader("Authorization"))
            .filter(authHeader -> authHeader.startsWith("Bearer"))
            .map(authHeader -> authHeader.substring("Bearer".length()).trim());
  }

  private Optional<Integer> extractUserId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      return Optional.ofNullable(claimsSet.getIntegerClaim("userId"));
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private boolean handleRateLimiting(int userId, HttpServletResponse response) {
    if (rateLimitingService.isAllowed(userId)) {
      return true;
    } else {
      return writeErrorResponse(response, 429, "Too many requests");
    }
  }

  private boolean handleInvalidToken(HttpServletResponse response) {
    return writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
  }

  private boolean writeErrorResponse(HttpServletResponse response, int statusCode, String message) {
    response.setStatus(statusCode);
    try {
      response.getWriter().write(message);
    } catch (IOException e) {
      System.out.println("error occurred ");
    }
    return false;
  }
}
