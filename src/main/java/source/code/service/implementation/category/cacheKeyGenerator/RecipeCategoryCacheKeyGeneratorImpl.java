package source.code.service.implementation.category.cacheKeyGenerator;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.recipe.RecipeCategory;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class RecipeCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<RecipeCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.RECIPE_CATEGORIES.name();
	}

}
