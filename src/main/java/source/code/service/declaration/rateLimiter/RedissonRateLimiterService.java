package source.code.service.declaration.rateLimiter;

public interface RedissonRateLimiterService {
    boolean isAllowed(int key);
}
