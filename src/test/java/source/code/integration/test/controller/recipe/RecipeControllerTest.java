package source.code.integration.test.controller.recipe;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.request.recipe.RecipeUpdateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
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
		RecipeCreateDto createDto = new RecipeCreateDto();
		createDto.setName("Test Recipe");
		createDto.setDescription("A new test recipe");
		createDto.setMinutesToPrepare((short) 30);
		createDto.setCategoryIds(List.of(1, 3));

		mockMvc
			.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpectAll(status().isCreated(), jsonPath("$.id").exists());
	}

	@RecipeSql
	@Test
	@DisplayName("PATCH - /{id} - Should update an existing recipe when owner")
	void updateRecipe() throws Exception {
		Utils.setUserContext(1);
		RecipeUpdateDto updateDto = new RecipeUpdateDto();
		updateDto.setName("Updated Stir Fry");
		updateDto.setDescription("Updated description");

		mockMvc
			.perform(patch("/api/recipes/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/recipes/1"))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Updated Stir Fry"));
	}

	@RecipeSql
	@Test
	@DisplayName("PATCH - /{id} - Should update an existing recipe when admin")
	void updateRecipeAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		RecipeUpdateDto updateDto = new RecipeUpdateDto();
		updateDto.setName("Admin Updated Recipe");

		mockMvc
			.perform(patch("/api/recipes/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/recipes/1")).andExpect(jsonPath("$.name").value("Admin Updated Recipe"));
	}

	@RecipeSql
	@Test
	@DisplayName("PATCH - /{id} - Should not update an existing recipe when not owner or admin")
	void updateRecipeNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);
		RecipeUpdateDto updateDto = new RecipeUpdateDto();
		updateDto.setName("Unauthorized Update");

		mockMvc
			.perform(patch("/api/recipes/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH  - /{id} - Should return 404 when recipe not found")
	void updateRecipeNotFound() throws Exception {
		Utils.setUserContext(1);
		RecipeUpdateDto updateDto = new RecipeUpdateDto();
		updateDto.setName("Missing Recipe");

		mockMvc
			.perform(patch("/api/recipes/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());
	}

	@RecipeSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete an existing recipe when owner")
	void deleteRecipe() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/recipes/1")).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/recipes/1")).andExpect(status().isNotFound());
	}

	@RecipeSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete an existing recipe when admin")
	void deleteRecipeAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		mockMvc.perform(delete("/api/recipes/2")).andExpect(status().isNoContent());
	}

	@RecipeSql
	@Test
	@DisplayName("DELETE - /{id} - Should not delete an existing recipe when not owner or admin")
	void deleteRecipeNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);
		mockMvc.perform(delete("/api/recipes/1")).andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("DELETE - /{id} - Should return 404 when recipe not found")
	void deleteRecipeNotFound() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/recipes/999")).andExpect(status().isNotFound());
	}

	@RecipeSql
	@Test
	@DisplayName("GET - /{id} - Should return a recipe by ID")
	void getRecipeById() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(get("/api/recipes/1"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(1), jsonPath("$.name").value("Vegetable Stir Fry"),
					jsonPath("$.description").value("Healthy vegetable dish"), jsonPath("$.isPublic").value(true),
					jsonPath("$.author.username").value("testuser"), jsonPath("$.author.id").value(1),
					jsonPath("$.author.imageName").value("user1_profile.jpg"), jsonPath("$.author.imageUrl").exists(),
					jsonPath("$.likesCount").value(0), jsonPath("$.dislikesCount").value(0),
					jsonPath("$.savesCount").value(1), jsonPath("$.views").value(0), jsonPath("$.liked").value(false),
					jsonPath("$.disliked").value(false), jsonPath("$.saved").value(true),
					jsonPath("$.totalCalories").value(8200.0), jsonPath("$.minutesToPrepare").value(15),
					jsonPath("$.categories").isArray(), jsonPath("$.categories.length()").value(2),
					jsonPath("$.instructions").isArray(), jsonPath("$.instructions.length()").value(2),
					jsonPath("$.foods").isArray(), jsonPath("$.foods.length()").value(1),
					jsonPath("$.foods[0].foodId").value(1), jsonPath("$.foods[0].foodName").value("Carrot"),
					jsonPath("$.foods[0].quantity").value(200), jsonPath("$.imageUrls", hasSize(2)));
	}

	@Test
	@DisplayName("GET - /{id} - Should return 404 when recipe not found")
	void getRecipeByIdNotFound() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(get("/api/recipes/999")).andExpect(status().isNotFound());
	}

	@RecipeSql
	@Test
	@DisplayName("GET - /{id} - Should return 403 when not owner or admin and recipe is private")
	void getRecipeByIdForbidden() throws Exception {
		Utils.setUserContext(3);
		mockMvc.perform(get("/api/recipes/4")).andExpect(status().isForbidden());
	}

	@WithMockUser
	@RecipeSql
	@Test
	@DisplayName("PATCH - /{id}/view - Should increment views for a recipe")
	void incrementViews() throws Exception {
		mockMvc.perform(patch("/api/recipes/1/view")).andExpect(status().isNoContent());
	}

	@WithMockUser
	@RecipeSql
	@Test
	@DisplayName("PATCH - /{id}/view - Should increment views multiple times")
	void incrementViewsMultipleTimes() throws Exception {
		mockMvc.perform(patch("/api/recipes/1/view")).andExpect(status().isNoContent());

		mockMvc.perform(patch("/api/recipes/1/view")).andExpect(status().isNoContent());

		mockMvc.perform(patch("/api/recipes/1/view")).andExpect(status().isNoContent());
	}

	@WithMockUser
	@Test
	@DisplayName("PATCH - /{id}/view - Should return 204 even for non-existent recipe")
	void incrementViewsNonExistentRecipe() throws Exception {
		mockMvc.perform(patch("/api/recipes/999/view")).andExpect(status().isNoContent());
	}

}
