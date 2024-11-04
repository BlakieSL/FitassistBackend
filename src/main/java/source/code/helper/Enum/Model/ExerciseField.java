package source.code.helper.Enum.Model;

public enum ExerciseField {
    TYPE("exerciseType"),
    EXPERTISE_LEVEL("expertiseLevel"),
    EQUIPMENT("equipment"),
    MECHANICS_TYPE("mechanicsType"),
    FORCE_TYPE("forceType"),
    TARGET_MUSCLE("exerciseTargetMuscles");

    private final String fieldName;

    ExerciseField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}