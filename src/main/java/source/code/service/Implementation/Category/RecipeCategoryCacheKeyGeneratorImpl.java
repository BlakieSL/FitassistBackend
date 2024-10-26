package source.code.service.Implementation.Category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Recipe.RecipeCategory;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;

@Service
public class RecipeCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<RecipeCategory> {
  @Override
  public String generateCacheKey() {
    return CacheKeys.RECIPE_CATEGORIES.name();
  }
}
