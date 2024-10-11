package source.code.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordUppercaseValidator implements ConstraintValidator<PasswordUppercaseDomain, String> {
  @Override
  public void initialize(PasswordUppercaseDomain constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null || password.isEmpty()) {
      return true;
    }
    return password.matches(".*[A-Z].*");
  }
}
