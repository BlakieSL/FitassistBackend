package source.code.helper.Enum.model.field;

public enum PlanField {
    LIKE,
    SAVE,
    TYPE("planType"),
    EQUIPMENT("exercises.equipment"),
    CATEGORY("planCategoryAssociations");

    private String fieldName;

    PlanField(String fieldName) {
        this.fieldName = fieldName;
    }

    PlanField() {

    }

    public String getFieldName() {
        return fieldName;
    }
}
