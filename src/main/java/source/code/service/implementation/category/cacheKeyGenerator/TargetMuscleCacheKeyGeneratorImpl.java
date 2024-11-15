package source.code.service.implementation.category.cacheKeyGenerator;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.exercise.TargetMuscle;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class TargetMuscleCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<TargetMuscle> {
    @Override
    public String generateCacheKey() {
        return CacheKeys.TARGET_MUSCLE.name();
    }
}
