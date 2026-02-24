package com.fitassist.backend.integration.utils;

import com.fitassist.backend.integration.config.MockMvcConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Import(MockMvcConfig.class)
@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = { "classpath:${schema.name}/schema/drop-schema.sql",
		"classpath:${schema.name}/schema/create-schema.sql" })
@Sql(scripts = { "classpath:${schema.name}/schema/drop-schema.sql" },
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public @interface TestSetup {

}
