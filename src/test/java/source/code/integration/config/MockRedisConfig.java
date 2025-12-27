package source.code.integration.config;

import static org.mockito.Mockito.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
		return new UniversalCacheMissManager();
	}

	@Bean
	@ConditionalOnProperty(name = "redis-flag.enabled", havingValue = "false", matchIfMissing = true)
	public RedissonClient redissonClient() {
		return mock(RedissonClient.class);
	}

	static class UniversalCacheMissManager implements CacheManager {

		private final ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>();

		@Override
		public Cache getCache(String name) {
			return caches.computeIfAbsent(name, CacheMissMock::new);
		}

		@Override
		public Collection<String> getCacheNames() {
			return Collections.unmodifiableSet(caches.keySet());
		}

		static class CacheMissMock implements Cache {

			private final String name;

			CacheMissMock(String name) {
				this.name = name;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Object getNativeCache() {
				return null;
			}

			@Override
			public ValueWrapper get(Object key) {
				return null;
			}

			@Override
			public <T> T get(Object key, Class<T> type) {
				return null;
			}

			@Override
			public <T> T get(Object key, Callable<T> valueLoader) {
				return null;
			}

			@Override
			public void put(Object key, Object value) {
			}

			@Override
			public ValueWrapper putIfAbsent(Object key, Object value) {
				return null;
			}

			@Override
			public void evict(Object key) {
			}

			@Override
			public void clear() {
			}

		}

	}

}
