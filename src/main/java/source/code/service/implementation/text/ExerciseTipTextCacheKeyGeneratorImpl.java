package source.code.service.implementation.text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.text.ExerciseTip;
import source.code.service.declaration.text.TextCacheKeyGenerator;

@Service
public class ExerciseTipTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<ExerciseTip> {
    @Override
    public String generateCacheKey(ExerciseTip entity) {
        return CacheKeys.EXERCISE_TIP.toString() + entity.getExercise().getId();
    }

    @Override
    public String generateCacheKeyForParent(int parentId) {
        return CacheKeys.EXERCISE_TIP.toString() + parentId;
    }
}
