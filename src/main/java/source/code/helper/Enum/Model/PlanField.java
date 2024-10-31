package source.code.helper.Enum.Model;

public enum PlanField {
  TYPE("planType"),
  DURATION("planDuration"),
  EXPERTISE_LEVEL("expertiseLevel"),
  CATEGORY("planCategoryAssociations");

  private final String fieldName;

  PlanField(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }
}
