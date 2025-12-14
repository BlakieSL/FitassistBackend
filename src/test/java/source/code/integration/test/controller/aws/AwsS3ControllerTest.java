package source.code.integration.test.controller.aws;

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
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.AwsS3ContainerInitializer;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.service.declaration.aws.AwsS3Service;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Import({MockRedisConfig.class, MockAwsSesConfig.class})
@ContextConfiguration(initializers = {MySqlContainerInitializer.class, AwsS3ContainerInitializer.class})
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
