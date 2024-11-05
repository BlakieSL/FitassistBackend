package source.code.service.implementation.category;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Exercise.TargetMuscle;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class ExerciseCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<TargetMuscle> {
    @Override
    public String generateCacheKey() {
        return CacheKeys.EXERCISE_CATEGORIES.name();
    }
}
