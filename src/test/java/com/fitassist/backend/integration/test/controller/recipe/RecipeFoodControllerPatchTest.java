package com.fitassist.backend.integration.test.controller.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class RecipeFoodControllerPatchTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@RecipeFoodSql
	@Test
	@DisplayName("PATCH - /{recipeId}/modify/{foodId} - Should update food in recipe when owner")
	public void updateFoodRecipe_ShouldUpdateFoodInRecipe() throws Exception {
		Utils.setUserContext(1);
		String request = """
				{
				    "quantity": 300
				}
				""";

		mockMvc.perform(patch("/api/recipe-food/1/modify/1").contentType(MediaType.APPLICATION_JSON).content(request))
			.andExpectAll(status().isNoContent());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("PATCH - /{recipeId}/modify/{foodId} - Should update food in recipe when admin")
	public void updateFoodRecipe_ShouldUpdateFoodInRecipeWhenAdmin() throws Exception {
		Utils.setAdminContext(2);
		String request = """
				{
				    "quantity": 300
				}
				""";

		mockMvc.perform(patch("/api/recipe-food/1/modify/1").contentType(MediaType.APPLICATION_JSON).content(request))
			.andExpectAll(status().isNoContent());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("PATCH - /{recipeId}/modify/{foodId} - Should return 403 when not owner or admin")
	public void updateFoodRecipe_ShouldReturn403WhenNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);

		String request = """
				{
				    "quantity": 300
				}
				""";

		mockMvc.perform(patch("/api/recipe-food/1/modify/1").contentType(MediaType.APPLICATION_JSON).content(request))
			.andExpectAll(status().isForbidden());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("PATCH - /{recipeId}/modify/{foodId} - Should return 404 when recipe food not found")
	public void updateFoodRecipe_ShouldReturn404WhenRecipeFoodNotFound() throws Exception {
		Utils.setUserContext(1);
		String request = """
				{
				    "quantity": 300
				}
				""";

		mockMvc.perform(patch("/api/recipe-food/5/modify/9").contentType(MediaType.APPLICATION_JSON).content(request))
			.andExpectAll(status().isNotFound());
	}

}
