package source.code.service.Implementation.Category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Plan.PlanCategory;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;

@Service
public class PlanCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<PlanCategory> {
  @Override
  public String generateCacheKey() {
    return CacheKeys.PLAN_CATEGORIES.name();
  }
}
