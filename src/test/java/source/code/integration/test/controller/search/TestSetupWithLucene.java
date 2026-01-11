package source.code.integration.test.controller.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@AutoConfigureMockMvc
@ActiveProfiles({ "test", "test-lucene" })
@SpringBootTest
@Sql(scripts = { "classpath:${schema.name}/schema/drop-schema.sql",
		"classpath:${schema.name}/schema/create-schema.sql" })
@Sql(scripts = { "classpath:${schema.name}/schema/drop-schema.sql" },
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public @interface TestSetupWithLucene {

}
