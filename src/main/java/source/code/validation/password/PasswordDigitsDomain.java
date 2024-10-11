package source.code.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordDigitsValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordDigitsDomain {
  String message() default "{com.example.lab10.PasswordDigitsDomain.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
