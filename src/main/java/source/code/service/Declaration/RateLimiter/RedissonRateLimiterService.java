package source.code.service.Declaration.RateLimiter;

public interface RedissonRateLimiterService {
    boolean isAllowed(int key);
}
