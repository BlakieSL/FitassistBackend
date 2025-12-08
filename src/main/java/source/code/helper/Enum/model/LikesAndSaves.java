package source.code.helper.Enum.model;

public enum LikesAndSaves {
    USER_EXERCISES("userExercises"),
    USER_FOODS("userFoods"),
    USER_RECIPES("userRecipes"),
    USER_PLANS("userPlans"),
    USER_ACTIVITIES("userActivities");

    private String fieldName;

    LikesAndSaves(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
