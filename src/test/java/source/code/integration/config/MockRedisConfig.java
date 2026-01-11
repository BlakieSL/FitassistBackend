package source.code.integration.config;

import static org.mockito.Mockito.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import source.code.auth.RateLimitingFilter;
import source.code.service.declaration.rateLimiter.RedissonRateLimiterService;

@TestConfiguration
public class MockRedisConfig {

	@Bean
	public RateLimitingFilter rateLimitingFilter() {
		return new RateLimitingFilter(null, null) {
			@Override
			protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
					@NotNull FilterChain filterChain) throws ServletException, IOException {
				filterChain.doFilter(request, response);
			}
		};
	}

	@Bean
	@ConditionalOnProperty(name = "redis-flag.enabled", havingValue = "false", matchIfMissing = true)
	public RedissonRateLimiterService mockService() {
		return new RedissonRateLimiterService() {
			@Override
			public boolean isAllowed(int key) {
				return false;
			}

			@Override
			public boolean isAllowed(String key) {
				return false;
			}
		};
	}

	@Bean
	@ConditionalOnProperty(name = "redis-flag.enabled", havingValue = "false", matchIfMissing = true)
	public CacheManager cacheManager() {
		return new NoOpCacheManager();
	}

	@Bean
	@ConditionalOnProperty(name = "redis-flag.enabled", havingValue = "false", matchIfMissing = true)
	public RedissonClient redissonClient() {
		return mock(RedissonClient.class);
	}

}
