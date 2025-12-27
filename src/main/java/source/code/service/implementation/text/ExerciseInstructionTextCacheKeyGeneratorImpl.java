package source.code.service.implementation.text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.text.ExerciseInstruction;
import source.code.service.declaration.text.TextCacheKeyGenerator;

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
