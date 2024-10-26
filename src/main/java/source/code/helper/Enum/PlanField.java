package source.code.helper.Enum;

public enum PlanField {
  TYPE,
  DURATION,
  EQUIPMENT,
  EXPERTISE_LEVEL;
  @Override
  public String toString() {
    return name() + "_";
  }
}
