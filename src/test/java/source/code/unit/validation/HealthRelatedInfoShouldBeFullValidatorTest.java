package source.code.unit.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.model.user.ActivityLevel;
import source.code.helper.Enum.model.user.Goal;
import source.code.model.user.User;
import source.code.validation.healthRelatedInfo.HealthInfoShouldBeFullDomain;
import source.code.validation.healthRelatedInfo.HealthRelatedInfoShouldBeFullValidator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class HealthRelatedInfoShouldBeFullValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private HealthInfoShouldBeFullDomain annotation;

    @InjectMocks
    private HealthRelatedInfoShouldBeFullValidator validator;

    @Test
    void isValid_shouldReturnTrueWhenAllFieldsAreNull() {
        User user = new User();
        user.setHeight(null);
        user.setWeight(null);
        user.setGoal(null);
        user.setActivityLevel(null);

        boolean result = validator.isValid(user, context);

        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueWhenAllFieldsAreSet() {
        User user = new User();
        user.setHeight(new BigDecimal("175.5"));
        user.setWeight(new BigDecimal("80.3"));
        user.setGoal(Goal.LOSE_WEIGHT);
        user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        boolean result = validator.isValid(user, context);

        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenOnlyHeightIsSet() {
        User user = new User();
        user.setHeight(new BigDecimal("175.5"));
        user.setWeight(null);
        user.setGoal(null);
        user.setActivityLevel(null);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenOnlyWeightIsSet() {
        User user = new User();
        user.setHeight(null);
        user.setWeight(new BigDecimal("80.3"));
        user.setGoal(null);
        user.setActivityLevel(null);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenOnlyGoalIsSet() {
        User user = new User();
        user.setHeight(null);
        user.setWeight(null);
        user.setGoal(Goal.BUILD_MUSCLE);
        user.setActivityLevel(null);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenOnlyActivityLevelIsSet() {
        User user = new User();
        user.setHeight(null);
        user.setWeight(null);
        user.setGoal(null);
        user.setActivityLevel(ActivityLevel.VERY_ACTIVE);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenHeightIsMissing() {
        User user = new User();
        user.setHeight(null);
        user.setWeight(new BigDecimal("80.3"));
        user.setGoal(Goal.MAINTAIN_WEIGHT);
        user.setActivityLevel(ActivityLevel.LIGHTLY_ACTIVE);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenWeightIsMissing() {
        User user = new User();
        user.setHeight(new BigDecimal("175.5"));
        user.setWeight(null);
        user.setGoal(Goal.LOSE_WEIGHT);
        user.setActivityLevel(ActivityLevel.SEDENTARY);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenGoalIsMissing() {
        User user = new User();
        user.setHeight(new BigDecimal("175.5"));
        user.setWeight(new BigDecimal("80.3"));
        user.setGoal(null);
        user.setActivityLevel(ActivityLevel.SUPER_ACTIVE);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenActivityLevelIsMissing() {
        User user = new User();
        user.setHeight(new BigDecimal("175.5"));
        user.setWeight(new BigDecimal("80.3"));
        user.setGoal(Goal.BUILD_MUSCLE);
        user.setActivityLevel(null);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseWhenMultipleFieldsAreMissing() {
        User user = new User();
        user.setHeight(new BigDecimal("175.5"));
        user.setWeight(null);
        user.setGoal(null);
        user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        boolean result = validator.isValid(user, context);

        assertFalse(result);
    }
}
