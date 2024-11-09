package source.code.helper.Enum.model.field;

public enum PlanField {
    TYPE("planType"),
    DURATION("planDuration"),
    EXPERTISE_LEVEL("expertiseLevel"),
    EQUIPMENT("exercises.equipment"),
    CATEGORY("planCategoryAssociations");

    private final String fieldName;

    PlanField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
