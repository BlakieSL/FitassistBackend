package source.code.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordSpecialValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordSpecialDomain {
  String message() default "{com.example.lab10.PasswordSpecialDomain.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
