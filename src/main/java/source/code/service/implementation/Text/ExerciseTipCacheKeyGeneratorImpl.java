package source.code.service.implementation.Text;

import org.springframework.stereotype.Service;
import source.code.model.Exercise.ExerciseTip;
import source.code.service.declaration.Text.CacheKeyGenerator;

@Service("exerciseTipCacheKeyGenerator")
public class ExerciseTipCacheKeyGeneratorImpl implements CacheKeyGenerator<ExerciseTip> {
  private static final String CACHE_PREFIX = "exerciseTip_";
  @Override
  public String generateCacheKey(ExerciseTip entity) {
    return CACHE_PREFIX + entity.getExercise().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CACHE_PREFIX + parentId;
  }
}
