package com.fitassist.backend.service.implementation.category.cacheKeyGenerator;

import com.fitassist.backend.config.cache.CacheKeys;
import com.fitassist.backend.model.activity.ActivityCategory;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class ActivityCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ActivityCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.ACTIVITY_CATEGORIES.name();
	}

}
