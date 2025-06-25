package source.code.helper.Enum.model.field;

public enum RecipeField {
    SAVE,
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
