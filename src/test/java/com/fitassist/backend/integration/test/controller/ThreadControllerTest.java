package com.fitassist.backend.integration.test.controller;

import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

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
