package source.code.integration.test.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import source.code.helper.Enum.model.user.ActivityLevel;
import source.code.helper.Enum.model.user.Gender;
import source.code.helper.Enum.model.user.Goal;
import source.code.integration.config.TestConfig;
import source.code.integration.containers.MySqlRedisAwsContainers;
import source.code.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Import(TestConfig.class)
public class UserHealthRelatedInfoTest extends MySqlRedisAwsContainers {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("validate - Should validate user when full health-related information provided")
    public void validateUserWithFullHealthInfo() {
        User user = createBasicUser();
        user.setHeight(new BigDecimal("180.5"));
        user.setWeight(new BigDecimal("75.5"));
        user.setGoal(Goal.LOSE_WEIGHT);
        user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("validate - Should validate user when no health-related information provided")
    public void validateUserWithoutHealthInfo() {
        User user = createBasicUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("validate - Should not validate user when provided partial health-related information")
    public void validateUserWithPartialHealthInfo() {
        User user = createBasicUser();
        user.setHeight(new BigDecimal("180.5"));
        user.setWeight(new BigDecimal("75.5"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        boolean healthInfoErrorFound = violations.stream()
                .anyMatch(v ->
                        v.getConstraintDescriptor().getAnnotation()
                                .annotationType().getSimpleName()
                                .equals("HealthInfoShouldBeFullDomain"));

        assertTrue(healthInfoErrorFound);
    }

    private User createBasicUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr/.6Bphm5Jx1h6HZ3L6f5zVtQ1/B2O");
        user.setGender(Gender.FEMALE);
        user.setBirthday(LocalDate.of(1990, 1,1));
        return user;
    }
}
