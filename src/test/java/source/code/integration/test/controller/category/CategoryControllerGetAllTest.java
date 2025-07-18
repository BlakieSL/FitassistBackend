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
import source.code.integration.utils.TestSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=category")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class CategoryControllerGetAllTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @WithMockUser
    @CategorySql
    @Test
    @DisplayName("GET /FOOD - Should return food categories")
    void getFoodCategories() throws Exception {
        mockMvc.perform(get("/api/categories/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET /FOOD - Should return empty list for food categories")
    void getFoodCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/api/categories/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }

    @WithMockUser
    @CategorySql
    @Test
    @DisplayName("GET /ACTIVITY - Should return activity categories")
    void getActivityCategories() throws Exception {
        mockMvc.perform(get("/api/categories/ACTIVITY"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET /ACTIVITY - Should return empty list for activity categories")
    void getActivityCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/api/categories/ACTIVITY"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }

    @WithMockUser
    @CategorySql
    @Test
    @DisplayName("GET /EXERCISE - Should return exercise categories")
    void getExerciseCategories() throws Exception {
        mockMvc.perform(get("/api/categories/EXERCISE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET /EXERCISE - Should return empty list for exercise categories")
    void getExerciseCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/api/categories/EXERCISE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }

    @WithMockUser
    @CategorySql
    @Test
    @DisplayName("GET /RECIPE - Should return recipe categories")
    void getRecipeCategories() throws Exception {
        mockMvc.perform(get("/api/categories/RECIPE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET /RECIPE - Should return empty list for recipe categories")
    void getRecipeCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/api/categories/RECIPE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }

    @WithMockUser
    @CategorySql
    @Test
    @DisplayName("GET /PLAN - Should return plan categories")
    void getPlanCategories() throws Exception {
        mockMvc.perform(get("/api/categories/PLAN"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET /PLAN - Should return empty list for plan categories")
    void getPlanCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/api/categories/PLAN"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }
}
