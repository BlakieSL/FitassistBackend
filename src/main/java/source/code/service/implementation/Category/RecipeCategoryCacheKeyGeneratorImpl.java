package source.code.service.implementation.Category;

import org.springframework.stereotype.Service;
import source.code.model.Recipe.RecipeCategory;
import source.code.service.declaration.Category.CategoryCacheKeyGenerator;

@Service
public class RecipeCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<RecipeCategory> {
  @Override
  public String generateCacheKey() {
    return "recipeCategories";
  }
}
