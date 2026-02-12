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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class RecipeFoodControllerDeleteTest {

	@Autowired
	private MockMvc mockMvc;

	@RecipeFoodSql
	@Test
	@DisplayName("DELETE - /remove/{receipeId}/{foodId} - Should delete food from recipe, When user is owner")
	void deleteFoodFromRecipe() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/recipe-food/1/remove/1")).andExpect(status().isOk());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("DELETE - /remove/{receipeId}/{foodId} - Should delete food from recipe, When user is admin")
	void deleteFoodFromRecipeAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		mockMvc.perform(delete("/api/recipe-food/1/remove/1")).andExpect(status().isOk());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("DELETE - /remove/{receipeId}/{foodId} - Should return 403, When user is not owner or admin")
	void deleteFoodFromRecipeNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);
		mockMvc.perform(delete("/api/recipe-food/1/remove/1")).andExpect(status().isForbidden());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("DELETE - /remove/{receipeId}/{foodId} - Should return 404, When recipeFood does not exist")
	void deleteFoodFromRecipeNotFound() throws Exception {
		Utils.setAdminContext(2);
		mockMvc.perform(delete("/api/recipe-food/4/remove/5")).andExpect(status().isNotFound());
	}

}
