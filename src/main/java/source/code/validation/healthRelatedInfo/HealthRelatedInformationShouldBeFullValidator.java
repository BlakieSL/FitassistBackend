package source.code.validation.healthRelatedInfo;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import source.code.model.user.User;

public class HealthRelatedInformationShouldBeFullValidator implements ConstraintValidator<HealthInformationShouldBeFullDomain, User> {
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (allFieldsNull(user)) {
            return true;
        }

        return !anyFieldNull(user);
    }

    private boolean allFieldsNull(User user) {
        return user.getHeight() == null &&
                user.getWeight() == null &&
                user.getGoal() == null &&
                user.getActivityLevel() == null;
    }

    private boolean anyFieldNull(User user) {
        return user.getHeight() == null ||
                user.getWeight() == null ||
                user.getGoal() == null ||
                user.getActivityLevel() == null;
    }
}
