package source.code.service.implementation.category.cacheKeyGenerator;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.plan.PlanCategory;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class PlanCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<PlanCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.PLAN_CATEGORIES.name();
	}

}
