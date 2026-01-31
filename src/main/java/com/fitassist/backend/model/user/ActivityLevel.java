package com.fitassist.backend.model.user;

import java.math.BigDecimal;

public enum ActivityLevel {

	SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, SUPER_ACTIVE;

	public BigDecimal getActivityFactor() {
		return switch (this) {
			case SEDENTARY -> BigDecimal.valueOf(1.2);
			case LIGHTLY_ACTIVE -> BigDecimal.valueOf(1.375);
			case MODERATELY_ACTIVE -> BigDecimal.valueOf(1.55);
			case VERY_ACTIVE -> BigDecimal.valueOf(1.725);
			case SUPER_ACTIVE -> BigDecimal.valueOf(1.9);
		};
	}

}
