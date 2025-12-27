package source.code.unit.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.exercise.Exercise;
import source.code.model.text.ExerciseInstruction;
import source.code.service.implementation.text.ExerciseInstructionTextCacheKeyGeneratorImpl;

@ExtendWith(MockitoExtension.class)
public class ExerciseInstructionTextCacheKeyGeneratorTest {

	@InjectMocks
	private ExerciseInstructionTextCacheKeyGeneratorImpl keyGenerator;

	@Test
	public void generateCacheKey() {
		int exerciseId = 123;
		var exerciseInstruction = new ExerciseInstruction();
		var exercise = new Exercise();
		exercise.setId(exerciseId);
		exerciseInstruction.setExercise(exercise);

		String result = keyGenerator.generateCacheKey(exerciseInstruction);

		String expected = CacheKeys.EXERCISE_INSTRUCTION.toString() + exerciseId;
		assertEquals(expected, result);
	}

	@Test
	public void generateCacheKeyForParent() {
		int exerciseId = 123;

		String result = keyGenerator.generateCacheKeyForParent(exerciseId);

		String expected = CacheKeys.EXERCISE_INSTRUCTION.toString() + exerciseId;
		assertEquals(expected, result);
	}

}
