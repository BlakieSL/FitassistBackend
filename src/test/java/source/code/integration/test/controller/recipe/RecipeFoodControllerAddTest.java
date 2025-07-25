package source.code.integration.test.controller.recipe;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.recipe.RecipeFoodCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class RecipeFoodControllerAddTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /add/{recipeId}/{foodId} - Should add food to recipe, When user is owner")
    void addFoodToRecipe() throws Exception {
        Utils.setUserContext(1);

        var request = new RecipeFoodCreateDto();

        mockMvc.perform(post("/api/recipe-food/4/add/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isCreated());
    }

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /add/{recipeId}/{foodId} - Should add food to recipe, When user is admin")
    void addFoodToRecipeAsAdmin() throws Exception {
        Utils.setAdminContext(2);

        var request = new RecipeFoodCreateDto();

        mockMvc.perform(post("/api/recipe-food/4/add/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isCreated());
    }

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /add/{recipeId}/{foodId} - Should return 403, When user is not owner or admin")
    void addFoodToRecipeNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);

        var request = new RecipeFoodCreateDto();

        mockMvc.perform(post("/api/recipe-food/4/add/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /add/{recipeId}/add/{foodId} - Should return 404, When recipe does not exist")
    void addFoodToRecipeNotFound() throws Exception {
        Utils.setAdminContext(2);

        var request = new RecipeFoodCreateDto();

        mockMvc.perform(post("/api/recipe-food/999/add/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /add/{recipeId}/add/{foodId} - Should return 404, When food does not exist")
    void addFoodToRecipeFoodNotFound() throws Exception {
        Utils.setAdminContext(2);

        var request = new RecipeFoodCreateDto();

        mockMvc.perform(post("/api/recipe-food/1/add/999")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /add/{recipeId}/add/{foodId} - Should return 409, When food is already added to recipe")
    void addFoodToRecipeAlreadyAdded() throws Exception {
        Utils.setUserContext(1);

        var request = new RecipeFoodCreateDto();

        mockMvc.perform(post("/api/recipe-food/1/add/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
