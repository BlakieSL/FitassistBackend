package com.fitassist.backend.integration.test.controller.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitassist.backend.dto.request.recipe.RecipeFoodCreateDto;
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

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class RecipeFoodControllerAddTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should add food to recipe, When user is owner")
	void addFoodToRecipe() throws Exception {
		Utils.setUserContext(1);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(1, BigDecimal.valueOf(100))));

		mockMvc
			.perform(post("/api/recipe-food/4/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isCreated());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should add food to recipe, When user is admin")
	void addFoodToRecipeAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(1, BigDecimal.valueOf(100))));

		mockMvc
			.perform(post("/api/recipe-food/4/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isCreated());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should add multiple foods to recipe, When user is owner")
	void addMultipleFoodsToRecipe() throws Exception {
		Utils.setUserContext(1);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(1, BigDecimal.valueOf(100)),
						new RecipeFoodCreateDto.FoodQuantityPair(2, BigDecimal.valueOf(150))));

		mockMvc
			.perform(post("/api/recipe-food/4/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isCreated());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should return 403, When user is not owner or admin")
	void addFoodToRecipeNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(1, BigDecimal.valueOf(100))));

		mockMvc
			.perform(post("/api/recipe-food/4/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should return 404, When recipe does not exist")
	void addFoodToRecipeNotFound() throws Exception {
		Utils.setAdminContext(2);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(1, BigDecimal.valueOf(100))));

		mockMvc
			.perform(post("/api/recipe-food/999/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should return 404, When food does not exist")
	void addFoodToRecipeFoodNotFound() throws Exception {
		Utils.setAdminContext(2);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(999, BigDecimal.valueOf(100))));

		mockMvc
			.perform(post("/api/recipe-food/1/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@RecipeFoodSql
	@Test
	@DisplayName("POST - /add - Should return 409, When food is already added to recipe")
	void addFoodToRecipeAlreadyAdded() throws Exception {
		Utils.setUserContext(1);
		RecipeFoodCreateDto request = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(1, BigDecimal.valueOf(100))));

		mockMvc
			.perform(post("/api/recipe-food/1/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict());
	}

}
