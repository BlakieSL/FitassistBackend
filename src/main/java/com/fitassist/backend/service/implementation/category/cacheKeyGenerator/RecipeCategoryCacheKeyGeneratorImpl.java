package com.fitassist.backend.service.implementation.category.cacheKeyGenerator;

import com.fitassist.backend.config.cache.CacheKeys;
import com.fitassist.backend.model.recipe.RecipeCategory;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class RecipeCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<RecipeCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.RECIPE_CATEGORIES.name();
	}

}
