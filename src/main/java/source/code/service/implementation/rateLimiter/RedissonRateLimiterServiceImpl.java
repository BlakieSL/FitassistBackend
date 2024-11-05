package source.code.service.implementation.rateLimiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import source.code.service.declaration.rateLimiter.RedissonRateLimiterService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RedissonRateLimiterServiceImpl implements RedissonRateLimiterService {
    private final RedissonClient redissonClient;
    private final ConcurrentMap<Integer, RRateLimiter> rateLimiters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RRateLimiter> rateLimitersByKey = new ConcurrentHashMap<>(); // New map for generic keys

    public RedissonRateLimiterServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean isAllowed(int userId) {
        RRateLimiter rateLimiter = getRateLimiterForUserId(userId);
        return rateLimiter.tryAcquire(1);
    }

    @Override
    public boolean isAllowed(String key) {
        RRateLimiter rateLimiter = getRateLimiterForKey(key);
        boolean allowed = rateLimiter.tryAcquire(1);
        System.out.println("Key: " + key + " | Allowed: " + allowed);
        return allowed;
    }

    private RRateLimiter getRateLimiterForUserId(int userId) {
        return rateLimiters.computeIfAbsent(userId, this::createRateLimiterForUserId);
    }

    private RRateLimiter createRateLimiterForUserId(int userId) {
        String rateLimiterName = "rateLimiter:user:" + userId;
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimiterName);
        rateLimiter.trySetRate(RateType.OVERALL, 8, 1, RateIntervalUnit.MINUTES);
        return rateLimiter;
    }

    // New method to handle rate limiting for generic keys (non-auth endpoints)
    private RRateLimiter getRateLimiterForKey(String key) {
        return rateLimitersByKey.computeIfAbsent(key, this::createRateLimiterForKey);
    }

    private RRateLimiter createRateLimiterForKey(String key) {
        String rateLimiterName = "rateLimiter:key:" + key;
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimiterName);
        rateLimiter.trySetRate(RateType.OVERALL, 5, 1, RateIntervalUnit.MINUTES); // Configurable rate for non-auth endpoints
        return rateLimiter;
    }
}
