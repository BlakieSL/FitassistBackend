package source.code.service.implementation.category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.food.FoodCategory;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class FoodCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<FoodCategory> {
    @Override
    public String generateCacheKey() {
        return CacheKeys.FOOD_CATEGORIES.name();
    }
}
