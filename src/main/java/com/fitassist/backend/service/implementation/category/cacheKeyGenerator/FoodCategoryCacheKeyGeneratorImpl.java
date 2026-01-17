package com.fitassist.backend.service.implementation.category.cacheKeyGenerator;

import com.fitassist.backend.config.cache.CacheKeys;
import com.fitassist.backend.model.food.FoodCategory;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class FoodCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<FoodCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.FOOD_CATEGORIES.name();
	}

}
