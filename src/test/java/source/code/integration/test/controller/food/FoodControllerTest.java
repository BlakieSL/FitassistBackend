package source.code.integration.test.controller.food;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=food")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class FoodControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @FoodSql
    @Test
    @DisplayName("POST - / - Should create a new food when user is admin")
    void createFood_ShouldCreateNewFood_WhenUserIsAdmin() throws Exception {
        Utils.setAdminContext(1);
        FoodCreateDto dto = new FoodCreateDto();
        dto.setName("Apple");
        dto.setCalories(BigDecimal.valueOf(95));
        dto.setProtein(BigDecimal.valueOf(0.5));
        dto.setFat(BigDecimal.valueOf(0.3));
        dto.setCarbohydrates(BigDecimal.valueOf(25.0));
        dto.setCategoryId(1);

        mockMvc.perform(post("/api/foods")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Apple"),
                        jsonPath("$.calories").value(95)
                );
    }

    @Test
    @DisplayName("POST - / - Should return 403 when user is not admin")
    void createFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
        Utils.setUserContext(1);
        FoodCreateDto dto = new FoodCreateDto();
        dto.setName("Apple");
        dto.setCalories(BigDecimal.valueOf(95));
        dto.setProtein(BigDecimal.valueOf(0.5));
        dto.setFat(BigDecimal.valueOf(0.3));
        dto.setCarbohydrates(BigDecimal.valueOf(25.0));
        dto.setCategoryId(1);

        mockMvc.perform(post("/api/foods")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isForbidden());
    }

    @FoodSql
    @Test
    @DisplayName("PATCH - /{id} - Should update food when user is admin")
    void updateFood_ShouldUpdateFood_WhenUserIsAdmin() throws Exception {
        Utils.setAdminContext(1);
        String patch = """
                {
                    "name": "Updated Food"
                }
                """;

        mockMvc.perform(patch("/api/foods/1")
                        .contentType("application/json-patch+json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when user is not admin")
    void updateFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
        Utils.setUserContext(1);
        String patch = """
                {
                    "name": "Updated Food"
                }
                """;

        mockMvc.perform(patch("/api/foods/1")
                        .contentType("application/json-patch+json")
                        .content(patch))
                .andExpectAll(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when food does not exist")
    void updateFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
        Utils.setAdminContext(1);
        String patch = """
                {
                    "name": "Updated Food"
                }
                """;

        mockMvc.perform(patch("/api/foods/999")
                        .contentType("application/json-patch+json")
                        .content(patch))
                .andExpectAll(status().isNotFound());
    }

    @FoodSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete food when user is admin")
    void deleteFood_ShouldDeleteFood_WhenUserIsAdmin() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(delete("/api/foods/1"))
                .andExpectAll(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE - /{id} - Should return 403 when user is not admin")
    void deleteFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(delete("/api/foods/1"))
                .andExpectAll(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when food does not exist")
    void deleteFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(delete("/api/foods/999"))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("GET - /{id} - Should return food when it exists")
    void getFood_ShouldReturnFood_WhenItExists() throws Exception {
        mockMvc.perform(get("/api/foods/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Apple"),
                        jsonPath("$.calories").value(95)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("GET - /{id} - Should return 404 when food does not exist")
    void getFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/foods/999"))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("GET - / - Should return all foods")
    void getAllFoods_ShouldReturnAllFoods() throws Exception {
        mockMvc.perform(get("/api/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Apple"),
                        jsonPath("$[1].id").value(2),
                        jsonPath("$[1].name").value("Banana")
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - / - Should return empty list when no foods exist")
    void getAllFoods_ShouldReturnEmptyList_WhenNoFoodsExist() throws Exception {
        mockMvc.perform(get("/api/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /{id}/calculate-macros - Should calculate food macros")
    void calculateFoodMacros_ShouldCalculateMacros_WhenRequestIsValid() throws Exception {
        Utils.setUserContext(1);
        CalculateFoodMacrosRequestDto request = new CalculateFoodMacrosRequestDto();
        request.setQuantity(BigDecimal.valueOf(100));
        mockMvc.perform(post("/api/foods/1/calculate-macros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.calories").value(95)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("POST - /{id}/calculate-macros - Should return 404 when food does not exist")
    void calculateFoodMacros_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
        Utils.setUserContext(1);
        CalculateFoodMacrosRequestDto request = new CalculateFoodMacrosRequestDto();
        request.setQuantity(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/foods/999/calculate-macros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isNotFound());
    }
}