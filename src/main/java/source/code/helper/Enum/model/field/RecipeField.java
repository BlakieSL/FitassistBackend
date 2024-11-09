package source.code.helper.Enum.model.field;

public enum RecipeField {
    CATEGORY("recipeCategoryAssociations"),
    FOODS("recipeFoods");
    private final String fieldName;

    RecipeField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
