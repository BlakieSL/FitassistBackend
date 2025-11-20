package source.code.unit.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.exercise.Exercise;
import source.code.model.text.ExerciseInstruction;
import source.code.service.implementation.text.ExerciseInstructionTextCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExerciseInstructionTextCacheKeyGeneratorTest {
    @InjectMocks
    private ExerciseInstructionTextCacheKeyGeneratorImpl keyGenerator;

    @Test
    @DisplayName("generateCacheKey - Should generate correct key for exercise instruction entity")
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
    @DisplayName("generateCacheKeyForParent - Should generate correct key for exercise ID")
    public void generateCacheKeyForParent() {
        int exerciseId = 123;

        String result = keyGenerator.generateCacheKeyForParent(exerciseId);

        String expected = CacheKeys.EXERCISE_INSTRUCTION.toString() + exerciseId;
        assertEquals(expected, result);
    }
}
