package source.code.integration.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockSearchConfig {

	@Primary
	@Bean
	public ApplicationRunner disableLuceneAutoIndex() {
		return args -> {
		};
	}

}
