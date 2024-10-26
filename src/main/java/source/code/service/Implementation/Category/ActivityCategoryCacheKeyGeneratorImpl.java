package source.code.service.Implementation.Category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Activity.ActivityCategory;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;

@Service
public class ActivityCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ActivityCategory> {
  @Override
  public String generateCacheKey() {
    return CacheKeys.ACTIVITY_CATEGORIES.name();
  }
}
