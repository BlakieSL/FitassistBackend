package source.code.helper.Enum.Model;

public enum ActivityField {
    CATEGORY("activityCategory"),
    MET("met");

    private final String fieldName;

    ActivityField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
