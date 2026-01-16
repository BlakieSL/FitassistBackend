package com.fitassist.backend.integration.test.controller.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.config.TestAwsS3Config;
import com.fitassist.backend.integration.containers.AwsS3ContainerInitializer;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Import({ MockRedisConfig.class, MockAwsSesConfig.class, TestAwsS3Config.class })
@ContextConfiguration(initializers = { MySqlContainerInitializer.class, AwsS3ContainerInitializer.class })
public class AwsS3ControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AwsS3Service awsS3Service;

	private String imageName;

	@BeforeEach
	void setUp() {
		byte[] imageBytes = "fake-image-data".getBytes(StandardCharsets.UTF_8);
		imageName = awsS3Service.uploadImage(imageBytes);
	}

	@WithMockUser
	@Test
	@DisplayName("GET /api/s3/{imageName} - Should return a presigned URL for the image")
	void getImageUrl_returnsPresignedUrl() throws Exception {
		mockMvc.perform(get("/api/s3/" + imageName))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(imageName)));
	}

}
