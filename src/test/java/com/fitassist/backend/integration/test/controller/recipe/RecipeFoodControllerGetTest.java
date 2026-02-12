package com.fitassist.backend.integration.test.controller.recipe;

import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.integration.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class RecipeFoodControllerGetTest {

	@Autowired
	private MockMvc mockMvc;

	@RecipeFoodSql
	@Test
	@DisplayName("GET - /{recipeId}/foods - Should get foods by recipe when owner")
	void getFoodsByRecipe_ShouldReturnFoodsWhenOwner() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(get("/api/recipe-food/1/foods")).andExpectAll(status().isOk(), jsonPath("$", hasSize(1)));
	}

	@RecipeFoodSql
	@Test
	@DisplayName("GET - /{recipeId}/foods - Should get foods by recipe when admin")
	void getFoodsByRecipe_ShouldReturnFoodsWhenAdmin() throws Exception {
		Utils.setAdminContext(2);
		mockMvc.perform(get("/api/recipe-food/1/foods")).andExpectAll(status().isOk(), jsonPath("$", hasSize(1)));
	}

	@RecipeFoodSql
	@Test
	@DisplayName("GET - /{recipeId}/foods - Should get foods by recipe when public")
	void getFoodsByRecipe_ShouldReturnFoodsWhenPublic() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(get("/api/recipe-food/1/foods")).andExpectAll(status().isOk(), jsonPath("$", hasSize(1)));
	}

	@RecipeFoodSql
	@Test
	@DisplayName("GET - /{recipeId}/foods - Should return 403 when not owner or admin")
	void getFoodsByRecipe_ShouldReturnForbiddenWhenNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);
		mockMvc.perform(get("/api/recipe-food/4/foods")).andExpectAll(status().isForbidden());
	}

}
