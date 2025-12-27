package source.code.integration.test.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;

@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class ThreadControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadControllerTest.class);

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Test
	void shouldRunOnVirtualThread() {
		RestTemplate restTemplate = new RestTemplate();

		try {
			ResponseEntity<String> response = restTemplate
				.getForEntity(baseUrl + port + "/api/virtual-threads/thread-info", String.class);

			LOGGER.info("RESPONSE STATUS: {}", response.getStatusCode());
			LOGGER.info("RESPONSE BODY: {}", response.getBody());

			assertTrue(response.getStatusCode().is2xxSuccessful());
			assertTrue(response.getBody().contains("\"isVirtual\":true"));

		}
		catch (Exception e) {
			LOGGER.error("Test failed with exception", e);
			throw e;
		}
	}

}
