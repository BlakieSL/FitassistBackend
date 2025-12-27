package source.code.integration.test.controller.workoutSetExercise;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SqlGroup({
		@Sql(scripts = "classpath:workoutSet/data/insert-data.sql",
				executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "classpath:workoutSet/data/remove-data.sql",
				executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) })
public @interface WorkoutSetExerciseSql {

}
