package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Text.ExerciseInstruction;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
public class ExerciseInstructionTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<ExerciseInstruction> {
  @Override
  public String generateCacheKey(ExerciseInstruction entity) {
    return CacheKeys.EXERCISE_INSTRUCTION.toString() + entity.getExercise().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CacheKeys.EXERCISE_INSTRUCTION.toString() + parentId;
  }
}
