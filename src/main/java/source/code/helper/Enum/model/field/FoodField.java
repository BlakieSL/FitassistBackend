package source.code.helper.Enum.model.field;

public enum FoodField {
    SAVE,
    CALORIES("calories"),
    PROTEIN("protein"),
    FAT("fat"),
    CARBOHYDRATES("carbohydrates"),
    CATEGORY("foodCategory");

    private String fieldName;

    FoodField(String fieldName) {
        this.fieldName = fieldName;
    }

    FoodField() {

    }

    public String getFieldName() {
        return fieldName;
    }
}
