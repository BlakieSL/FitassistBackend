package com.fitassist.backend.service.implementation.category.cacheKeyGenerator;

import com.fitassist.backend.config.cache.CacheKeys;
import com.fitassist.backend.model.plan.PlanCategory;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class PlanCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<PlanCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.PLAN_CATEGORIES.name();
	}

}
