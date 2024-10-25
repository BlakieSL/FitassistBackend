package source.code.service.implementation.Text;

import org.springframework.stereotype.Service;
import source.code.model.Text.ExerciseInstruction;
import source.code.service.declaration.Text.TextCacheKeyGenerator;

@Service("exerciseInstructionCacheKeyGenerator")
public class ExerciseInstructionTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<ExerciseInstruction> {
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
