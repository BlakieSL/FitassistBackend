package source.code.helper.Enum;

public enum ExerciseField {
  TYPE,
  EXPERTISE_LEVEL,
  EQUIPMENT,
  MECHANICS_TYPE,
  FORCE_TYPE;
  @Override
  public String toString() {
    return name() + "_";
  }
}
