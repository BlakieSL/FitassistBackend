package source.code.integration.test.controller.recipe;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=recipe")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @RecipeSql
    @Test
    @DisplayName("POST - / - Should create a new recipe")
    void createRecipe() throws Exception {
        Utils.setUserContext(1);
        String request = """
            {
                "name": "Test Recipe",
                "description": "A new test recipe",
                "foods": [
                    {"foodId": 1, "quantity": 200},
                    {"foodId": 2, "quantity": 150}
                ],
                "categories": [1, 3]
            }
            """;

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists()
                );
    }

    @RecipeSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing recipe when owner")
    void updateRecipe() throws Exception {
        Utils.setUserContext(1);
        String request = """
            {
                "name": "Updated Stir Fry",
                "description": "Updated description"
            }
            """;

        mockMvc.perform(patch("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Stir Fry"));
    }

    @RecipeSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing recipe when admin")
    void updateRecipeAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        String request = """
            {
                "name": "Admin Updated Recipe"
            }
            """;

        mockMvc.perform(patch("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(jsonPath("$.name").value("Admin Updated Recipe"));
    }

    @RecipeSql
    @Test
    @DisplayName("PATCH - /{id} - Should not update an existing recipe when not owner or admin")
    void updateRecipeNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);
        String request = "{\"name\":\"Unauthorized Update\"}";

        mockMvc.perform(patch("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH  - /{id} - Should return 404 when recipe not found")
    void updateRecipeNotFound() throws Exception {
        Utils.setUserContext(1);
        String request = "{\"name\":\"Missing Recipe\"}";

        mockMvc.perform(patch("/api/recipes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @RecipeSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing recipe when owner")
    void deleteRecipe() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isNotFound());
    }

    @RecipeSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing recipe when admin")
    void deleteRecipeAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        mockMvc.perform(delete("/api/recipes/2"))
                .andExpect(status().isNoContent());
    }

    @RecipeSql
    @Test
    @DisplayName("DELETE - /{id} - Should not delete an existing recipe when not owner or admin")
    void deleteRecipeNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when recipe not found")
    void deleteRecipeNotFound() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(delete("/api/recipes/999"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @RecipeSql
    @Test
    @DisplayName("GET - /{id} - Should return a recipe by ID")
    void getRecipeById() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/recipes/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Vegetable Stir Fry")
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{id} - Should return 404 when recipe not found")
    void getRecipeByIdNotFound() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @RecipeSql
    @Test
    @DisplayName("GET - / - Should return all recipes")
    void getAllRecipes() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/recipes"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(3)),
                        jsonPath("$[0].name").value("Vegetable Stir Fry")
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - / - Should return empty list when no recipes")
    void getAllRecipesEmpty() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/recipes"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );
    }
}
