package source.code.helper.Enum.Model;

public enum LikesAndSaves {
  LIKES,
  SAVES,
  USER_ACTIVITIES("userActivities"),
  USER_EXERCISES("userExercises"),
  USER_FOODS("userFoods"),
  USER_RECIPES("userRecipes"),
  USER_PLANS("userPlans");

  private String fieldName;

  LikesAndSaves(String fieldName) {
    this.fieldName = fieldName;
  }

  LikesAndSaves() {

  }

  public String getFieldName() {
    return fieldName;
  }
}
