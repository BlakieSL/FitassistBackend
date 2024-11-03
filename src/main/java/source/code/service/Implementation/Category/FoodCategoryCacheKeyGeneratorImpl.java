package source.code.service.Implementation.Category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Food.FoodCategory;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;

@Service
public class FoodCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<FoodCategory> {
  @Override
  public String generateCacheKey() {
    return CacheKeys.FOOD_CATEGORIES.name();
  }
}
