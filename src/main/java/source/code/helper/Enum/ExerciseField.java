package source.code.helper.Enum;

public enum ExerciseField {
  EXPERTISE_LEVEL,
  FORCE_TYPE,
  MECHANICS_TYPE,
  EQUIPMENT,
  TYPE;
  @Override
  public String toString() {
    return name() + "_";
  }
}
