package source.code.service.Implementation.RateLimiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import source.code.service.Declaration.RateLimiter.RedissonRateLimiterService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RedissonRateLimiterServiceImpl implements RedissonRateLimiterService {
  private final RedissonClient redissonClient;
  private final ConcurrentMap<Integer, RRateLimiter> rateLimiters = new ConcurrentHashMap<>();

  public RedissonRateLimiterServiceImpl(RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
  }

  @Override
  public boolean isAllowed(int userId) {
    RRateLimiter rateLimiter = getRateLimiterForUserId(userId);
    return rateLimiter.tryAcquire(1);
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
}
