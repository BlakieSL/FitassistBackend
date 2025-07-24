package source.code.integration.test.controller.plan;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@SqlGroup({
        @Sql(scripts = "classpath:plan/data/insert-data.sql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:plan/data/remove-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public @interface PlanSql {
}
