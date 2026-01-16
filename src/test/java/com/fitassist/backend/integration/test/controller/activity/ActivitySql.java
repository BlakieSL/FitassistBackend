package com.fitassist.backend.integration.test.controller.activity;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SqlGroup({
		@Sql(scripts = "classpath:activity/data/insert-data.sql",
				executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "classpath:activity/data/remove-data.sql",
				executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) })
public @interface ActivitySql {

}
