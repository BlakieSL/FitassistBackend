package source.code.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordDigitsValidator implements ConstraintValidator<PasswordDigitsDomain, String> {
  @Override
  public void initialize(PasswordDigitsDomain constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null || password.isEmpty()) {
      return true;
    }
    return password.matches(".*\\d.*");
  }
}
