package source.code.service.implementation.Text;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.model.Exercise.ExerciseInstruction;
import source.code.service.declaration.Text.CacheKeyGenerator;

@Service("exerciseInstructionCacheKeyGenerator")
public class ExerciseInstructionCacheKeyGeneratorImpl implements CacheKeyGenerator<ExerciseInstruction> {
  private static final String CACHE_PREFIX = "exerciseInstruction_";
  @Override
  public String generateCacheKey(ExerciseInstruction entity) {
    return CACHE_PREFIX + entity.getExercise().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CACHE_PREFIX + parentId;
  }
}
