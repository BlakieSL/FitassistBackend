package source.code.integration.test.controller.food;
import org.springframework.http.MediaType;

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
import source.code.dto.request.food.FoodUpdateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
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
        FoodUpdateDto updateDto = new FoodUpdateDto();
        updateDto.setName("Updated Food");

        mockMvc.perform(patch("/api/foods/1")
                        .contentType("application/json-patch+json")
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpectAll(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when user is not admin")
    void updateFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
        Utils.setUserContext(1);
        FoodUpdateDto updateDto = new FoodUpdateDto();
        updateDto.setName("Updated Food");

        mockMvc.perform(patch("/api/foods/1")
                        .contentType("application/json-patch+json")
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpectAll(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when food does not exist")
    void updateFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
        Utils.setAdminContext(1);
        FoodUpdateDto updateDto = new FoodUpdateDto();
        updateDto.setName("Updated Food");

        mockMvc.perform(patch("/api/foods/999")
                        .contentType("application/json-patch+json")
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpectAll(status().isNotFound());
    }

    @FoodSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete food when user is admin")
    void deleteFood_ShouldDeleteFood_WhenUserIsAdmin() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(delete("/api/foods/4"))
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

    @FoodSql
    @Test
    @DisplayName("GET - /{id} - Should return food with image URLs, recipes, saves count, and saved=true when user has saved it")
    void getFood_ShouldReturnFoodWithSavedTrue_WhenUserHasSavedIt() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/foods/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Apple"),
                        jsonPath("$.calories").value(95),
                        jsonPath("$.imageUrls").isArray(),
                        jsonPath("$.savesCount").value(2),
                        jsonPath("$.saved").value(true),
                        jsonPath("$.recipes").isArray(),
                        jsonPath("$.recipes.length()").value(2),
                        jsonPath("$.recipes[0].id").exists(),
                        jsonPath("$.recipes[0].name").exists(),
                        jsonPath("$.recipes[0].description").exists(),
                        jsonPath("$.recipes[0].public").exists(),
                        jsonPath("$.recipes[0].authorUsername").exists(),
                        jsonPath("$.recipes[0].authorId").exists()
                );
    }

    @FoodSql
    @Test
    @DisplayName("GET - /{id} - Should return food with saved=false when user has not saved it")
    void getFood_ShouldReturnFoodWithSavedFalse_WhenUserHasNotSavedIt() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(get("/api/foods/2"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(2),
                        jsonPath("$.name").value("Banana"),
                        jsonPath("$.savesCount").value(1),
                        jsonPath("$.saved").value(false)
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
    @DisplayName("GET - / - Should return paginated foods with default pagination")
    void getAllFoods_ShouldReturnPaginatedFoods() throws Exception {
        mockMvc.perform(get("/api/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[0].name").value("Apple"),
                        jsonPath("$.content[1].id").value(2),
                        jsonPath("$.content[1].name").value("Banana"),
                        jsonPath("$.page.totalElements").exists(),
                        jsonPath("$.page.totalPages").exists(),
                        jsonPath("$.page.size").exists(),
                        jsonPath("$.page.number").value(0)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("GET - / - Should return paginated foods with custom page size")
    void getAllFoods_ShouldReturnPaginatedFoods_WithCustomPageSize() throws Exception {
        mockMvc.perform(get("/api/foods")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.length()").value(2),
                        jsonPath("$.page.size").value(2),
                        jsonPath("$.page.number").value(0)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("GET - / - Should return sorted foods")
    void getAllFoods_ShouldReturnSortedFoods() throws Exception {
        mockMvc.perform(get("/api/foods")
                        .param("sort", "name,desc"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].name").value("Greek Yogurt")
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - / - Should return empty page when no foods exist")
    void getAllFoods_ShouldReturnEmptyPage_WhenNoFoodsExist() throws Exception {
        mockMvc.perform(get("/api/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isEmpty(),
                        jsonPath("$.page.totalElements").value(0)
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
