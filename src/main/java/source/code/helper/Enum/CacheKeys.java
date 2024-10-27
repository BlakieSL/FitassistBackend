package source.code.helper.Enum;

public enum CacheKeys {
  ACTIVITY_CATEGORIES,
  EXERCISE_CATEGORIES,
  FOOD_CATEGORIES,
  PLAN_CATEGORIES,
  RECIPE_CATEGORIES,

  EXERCISE_INSTRUCTION,
  EXERCISE_TIP,
  PLAN_INSTRUCTION,
  RECIPE_INSTRUCTION;

  @Override
  public String toString() {
    return name() + "_";
  }
}
