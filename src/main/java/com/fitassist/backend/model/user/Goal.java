package com.fitassist.backend.model.user;

import java.math.BigDecimal;

public enum Goal {

	LOSE_WEIGHT, MAINTAIN_WEIGHT, BUILD_MUSCLE;

	private static final BigDecimal CALORIC_CONST = BigDecimal.valueOf(200);

	public BigDecimal normalizeBasedOnGoal(BigDecimal tdee) {
		return switch (this) {
			case LOSE_WEIGHT -> tdee.subtract(CALORIC_CONST);
			case MAINTAIN_WEIGHT -> tdee;
			case BUILD_MUSCLE -> tdee.add(CALORIC_CONST);
		};
	}

}
