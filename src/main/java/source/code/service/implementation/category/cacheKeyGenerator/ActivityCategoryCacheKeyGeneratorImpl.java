package source.code.service.implementation.category.cacheKeyGenerator;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.activity.ActivityCategory;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class ActivityCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ActivityCategory> {

	@Override
	public String generateCacheKey() {
		return CacheKeys.ACTIVITY_CATEGORIES.name();
	}

}
