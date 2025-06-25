package source.code.helper.Enum.model.field;

public enum ActivityField {
    SAVE,
    CATEGORY("activityCategory"),
    MET("met");

    private String fieldName;

    ActivityField(String fieldName) {
        this.fieldName = fieldName;
    }

    ActivityField() {

    }

    public String getFieldName() {
        return fieldName;
    }
}
