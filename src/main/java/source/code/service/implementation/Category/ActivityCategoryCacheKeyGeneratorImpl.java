package source.code.service.implementation.Category;

import org.springframework.stereotype.Service;
import source.code.model.Activity.ActivityCategory;
import source.code.service.declaration.Category.CategoryCacheKeyGenerator;

@Service
public class ActivityCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ActivityCategory> {
  @Override
  public String generateCacheKey() {
    return "activityCategories";
  }
}
