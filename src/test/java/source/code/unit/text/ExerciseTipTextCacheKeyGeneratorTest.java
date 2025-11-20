package source.code.unit.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.exercise.Exercise;
import source.code.model.text.ExerciseTip;
import source.code.service.implementation.text.ExerciseTipTextCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExerciseTipTextCacheKeyGeneratorTest {
    @InjectMocks
    private ExerciseTipTextCacheKeyGeneratorImpl keyGenerator;

    @Test
    public void generateCacheKey() {
        int exerciseId = 123;
        ExerciseTip exerciseTip = new ExerciseTip();
        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        exerciseTip.setExercise(exercise);

        String result = keyGenerator.generateCacheKey(exerciseTip);

        String expected = CacheKeys.EXERCISE_TIP.toString() + exerciseId;
        assertEquals(expected, result);
    }

    @Test
    public void generateCacheKeyForParent() {
        int exerciseId = 123;

        String result = keyGenerator.generateCacheKeyForParent(exerciseId);

        String expected = CacheKeys.EXERCISE_TIP.toString() + exerciseId;
        assertEquals(expected, result);
    }
}
