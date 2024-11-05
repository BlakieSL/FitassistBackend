package source.code.service.implementation.category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Recipe.RecipeCategory;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class RecipeCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<RecipeCategory> {
    @Override
    public String generateCacheKey() {
        return CacheKeys.RECIPE_CATEGORIES.name();
    }
}
