package source.code.integration.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import source.code.auth.RateLimitingFilter;

import java.io.IOException;

@TestConfiguration
public class MockRateLimitingConfig {
    @Bean
    public RateLimitingFilter rateLimitingFilter() {
        return new RateLimitingFilter(null, null) {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                filterChain.doFilter(request, response);
            }
        };
    }
}
