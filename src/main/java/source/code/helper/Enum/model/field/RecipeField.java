package source.code.helper.Enum.model.field;

public enum RecipeField {
    SAVE,
    SAVED_BY_USER,
    LIKED_BY_USER,
    DISLIKED_BY_USER,
    CREATED_BY_USER,
    LIKE,
    CATEGORY("recipeCategoryAssociations"),
    FOODS("recipeFoods");
    private String fieldName;

    RecipeField(String fieldName) {
        this.fieldName = fieldName;
    }

    RecipeField() {

    }

    public String getFieldName() {
        return fieldName;
    }
}
