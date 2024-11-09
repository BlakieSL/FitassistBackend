package source.code.helper.Enum.model.field;

public enum FoodField {
    CALORIES("calories"),
    PROTEIN("protein"),
    FAT("fat"),
    CARBOHYDRATES("carbohydrates"),
    CATEGORY("foodCategory");

    private final String fieldName;

    FoodField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
