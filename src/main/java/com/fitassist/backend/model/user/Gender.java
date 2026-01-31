package com.fitassist.backend.model.user;

import java.math.BigDecimal;

public enum Gender {

	MALE, FEMALE;

	public BigDecimal calculateBMR(BigDecimal weight, BigDecimal height, int age) {
		BigDecimal base = BigDecimal.valueOf(10)
			.multiply(weight)
			.add(BigDecimal.valueOf(6.25).multiply(height))
			.subtract(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(age)));

		return switch (this) {
			case MALE -> base.add(BigDecimal.valueOf(5));
			case FEMALE -> base.add(BigDecimal.valueOf(-161));
		};
	}

}
