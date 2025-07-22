package source.code.helper.Enum.model.field;

import source.code.model.user.TypeOfInteraction;

public enum PlanField {
    LIKE(TypeOfInteraction.LIKE),
    SAVE(TypeOfInteraction.SAVE),
    TYPE("planType"),
    EQUIPMENT("exercises.equipment"),
    CATEGORY("planCategoryAssociations");

    private final String fieldName;
    private final TypeOfInteraction interactionType;

    PlanField(String fieldName) {
        this.fieldName = fieldName;
        this.interactionType = null;
    }

    PlanField(TypeOfInteraction interactionType) {
        this.fieldName = null;
        this.interactionType = interactionType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TypeOfInteraction getInteractionType() {
        return interactionType;
    }
}