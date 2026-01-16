package com.fitassist.backend.service.declaration.cache;

public interface CacheService {

	void evictCache(String cacheName, Object key);

	void clearCache(String cacheName);

	void putCache(String cacheName, Object key, Object cachedData);

}
