package source.code.validation.media;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueUserMediaValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserMedia {

	String message() default "{UniqueUserMedia.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
