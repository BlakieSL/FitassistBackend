package com.fitassist.backend.validation.health;

import com.fitassist.backend.model.user.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HealthRelatedInfoShouldBeFullValidator implements ConstraintValidator<HealthInfoShouldBeFullDomain, User> {

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		if (allFieldsNull(user)) {
			return true;
		}

		return !anyFieldNull(user);
	}

	private boolean allFieldsNull(User user) {
		return user.getHeight() == null && user.getWeight() == null && user.getGoal() == null
				&& user.getActivityLevel() == null;
	}

	private boolean anyFieldNull(User user) {
		return user.getHeight() == null || user.getWeight() == null || user.getGoal() == null
				|| user.getActivityLevel() == null;
	}

}
