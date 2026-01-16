package com.fitassist.backend.config.cache;

public enum CacheKeys {

	ACTIVITY_CATEGORIES, TARGET_MUSCLE, FOOD_CATEGORIES, PLAN_CATEGORIES, RECIPE_CATEGORIES, THREAD_CATEGORIES,

	EXERCISE_INSTRUCTION, EXERCISE_TIP, PLAN_INSTRUCTION, RECIPE_INSTRUCTION;

	@Override
	public String toString() {
		return name() + "_";
	}

}
