package com.fitassist.backend.service.implementation.category.cacheKeyGenerator;

import com.fitassist.backend.config.cache.CacheKeys;
import com.fitassist.backend.model.thread.ThreadCategory;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class ThreadCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ThreadCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.THREAD_CATEGORIES.name();
	}

}
