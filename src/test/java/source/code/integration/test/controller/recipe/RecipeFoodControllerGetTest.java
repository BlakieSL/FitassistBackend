package source.code.integration.test.controller.recipe;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.recipe.FilterRecipesByFoodsDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class RecipeFoodControllerGetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @RecipeFoodSql
    @Test
    @DisplayName("GET - /{recipeId}/foods - Should get foods by recipe when owner")
    void getFoodsByRecipe_ShouldReturnFoodsWhenOwner() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/recipe-food/1/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1))
                );
    }

    @RecipeFoodSql
    @Test
    @DisplayName("GET - /{recipeId}/foods - Should get foods by recipe when admin")
    void getFoodsByRecipe_ShouldReturnFoodsWhenAdmin() throws Exception {
        Utils.setAdminContext(2);

        mockMvc.perform(get("/api/recipe-food/1/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1))
                );
    }

    @RecipeFoodSql
    @Test
    @DisplayName("GET - /{recipeId}/foods - Should get foods by recipe when public")
    void getFoodsByRecipe_ShouldReturnFoodsWhenPublic() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/recipe-food/1/foods"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1))
                );
    }

    @RecipeFoodSql
    @Test
    @DisplayName("GET - /{recipeId}/foods - Should return 403 when not owner or admin")
    void getFoodsByRecipe_ShouldReturnForbiddenWhenNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);

        mockMvc.perform(get("/api/recipe-food/4/foods"))
                .andExpectAll(status().isForbidden());
    }

    @RecipeFoodSql
    @Test
    @DisplayName("POST - /{filter}/foods - Should get recipes by foods")
    void getRecipesByFoods_ShouldReturnRecipesWhenOwner() throws Exception {
        Utils.setUserContext(1);

        var filter = FilterRecipesByFoodsDto.of(List.of(1));
        mockMvc.perform(post("/api/recipe-food/filter/foods")
                .content(objectMapper.writeValueAsString(filter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1))
                );
    }
}
