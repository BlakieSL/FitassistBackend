package source.code.service.Implementation.Category;

import org.springframework.stereotype.Service;
import source.code.model.Plan.PlanCategory;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;

@Service
public class PlanCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<PlanCategory> {
  @Override
  public String generateCacheKey() {
    return "planCategories";
  }
}
