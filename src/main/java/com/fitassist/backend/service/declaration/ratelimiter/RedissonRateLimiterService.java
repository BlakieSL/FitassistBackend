package com.fitassist.backend.service.declaration.ratelimiter;

public interface RedissonRateLimiterService {

	boolean isAllowed(int key);

	boolean isAllowed(String key);

}
