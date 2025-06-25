package source.code.helper.Enum.model.field;

public enum ExerciseField {
    SAVE,
    EXPERTISE_LEVEL("expertiseLevel"),
    EQUIPMENT("equipment"),
    MECHANICS_TYPE("mechanicsType"),
    FORCE_TYPE("forceType"),
    TARGET_MUSCLE("exerciseTargetMuscles");

    private String fieldName;

    ExerciseField() {}

    ExerciseField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}