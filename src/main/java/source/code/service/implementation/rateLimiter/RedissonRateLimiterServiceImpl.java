package source.code.service.implementation.rateLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import source.code.config.RedissonRateLimitConfig;
import source.code.service.declaration.rateLimiter.RedissonRateLimiterService;

@Service
public class RedissonRateLimiterServiceImpl implements RedissonRateLimiterService {

	private final RedissonClient redissonClient;

	private final RedissonRateLimitConfig rateLimitConfig;

	private final ConcurrentMap<Integer, RRateLimiter> rateLimiters = new ConcurrentHashMap<>();

	private final ConcurrentMap<String, RRateLimiter> rateLimitersByKey = new ConcurrentHashMap<>();

	public RedissonRateLimiterServiceImpl(RedissonClient redissonClient, RedissonRateLimitConfig rateLimitConfig) {
		this.redissonClient = redissonClient;
		this.rateLimitConfig = rateLimitConfig;
	}

	@Override
	public boolean isAllowed(int userId) {
		RRateLimiter rateLimiter = getRateLimiterForUserId(userId);
		return rateLimiter.tryAcquire(1);
	}

	@Override
	public boolean isAllowed(String key) {
		RRateLimiter rateLimiter = getRateLimiterForKey(key);
		return rateLimiter.tryAcquire(1);
	}

	private RRateLimiter getRateLimiterForUserId(int userId) {
		return rateLimiters.computeIfAbsent(userId, this::createRateLimiterForUserId);
	}

	private RRateLimiter createRateLimiterForUserId(int userId) {
		String rateLimiterName = "rateLimiter:user:" + userId;
		RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimiterName);
		rateLimiter.setRate(RateType.OVERALL, rateLimitConfig.getUserRate(), rateLimitConfig.getUserInterval(),
			RateIntervalUnit.MINUTES);
		return rateLimiter;
	}

	private RRateLimiter getRateLimiterForKey(String key) {
		return rateLimitersByKey.computeIfAbsent(key, this::createRateLimiterForKey);
	}

	private RRateLimiter createRateLimiterForKey(String key) {
		String rateLimiterName = "rateLimiter:key:" + key;
		RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimiterName);
		rateLimiter.setRate(RateType.OVERALL, rateLimitConfig.getKeyRate(), rateLimitConfig.getKeyInterval(),
			RateIntervalUnit.MINUTES);
		return rateLimiter;
	}

}
