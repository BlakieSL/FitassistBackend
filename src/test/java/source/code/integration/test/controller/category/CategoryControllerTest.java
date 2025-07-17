package source.code.integration.test.controller.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.containers.RedisContainerInitializer;
import source.code.integration.utils.TestSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=category")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser
    @CategorySql
    @Test
    @DisplayName("/{categoryType} - Should return all categories")
    void getAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("/{categoryType} - Should return empty list")
    void getAllCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/api/categories/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }

}
