package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.model.Text.ExerciseInstruction;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
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
