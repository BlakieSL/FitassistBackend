package com.fitassist.backend.integration.test.controller.food;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitassist.backend.dto.request.food.CalculateFoodMacrosRequestDto;
import com.fitassist.backend.dto.request.food.FoodCreateDto;
import com.fitassist.backend.dto.request.food.FoodUpdateDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
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
		FoodCreateDto dto = new FoodCreateDto("Apple", BigDecimal.valueOf(95), BigDecimal.valueOf(0.5),
				BigDecimal.valueOf(0.3), BigDecimal.valueOf(25.0), 1);

		mockMvc
			.perform(post("/api/foods").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpectAll(status().isCreated(), jsonPath("$.id").exists(), jsonPath("$.name").value("Apple"),
					jsonPath("$.foodMacros.calories").value(95));
	}

	@Test
	@DisplayName("POST - / - Should return 403 when user is not admin")
	void createFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
		Utils.setUserContext(1);
		FoodCreateDto dto = new FoodCreateDto("Apple", BigDecimal.valueOf(95), BigDecimal.valueOf(0.5),
				BigDecimal.valueOf(0.3), BigDecimal.valueOf(25.0), 1);

		mockMvc
			.perform(post("/api/foods").contentType(MediaType.APPLICATION_JSON)
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

		mockMvc
			.perform(patch("/api/foods/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());
	}

	@Test
	@DisplayName("PATCH - /{id} - Should return 403 when user is not admin")
	void updateFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
		Utils.setUserContext(1);
		FoodUpdateDto updateDto = new FoodUpdateDto();
		updateDto.setName("Updated Food");

		mockMvc
			.perform(patch("/api/foods/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH - /{id} - Should return 404 when food does not exist")
	void updateFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
		Utils.setAdminContext(1);
		FoodUpdateDto updateDto = new FoodUpdateDto();
		updateDto.setName("Updated Food");

		mockMvc
			.perform(patch("/api/foods/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNotFound());
	}

	@FoodSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete food when user is admin")
	void deleteFood_ShouldDeleteFood_WhenUserIsAdmin() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/foods/4")).andExpectAll(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE - /{id} - Should return 403 when user is not admin")
	void deleteFood_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/foods/1")).andExpectAll(status().isForbidden());
	}

	@Test
	@DisplayName("DELETE - /{id} - Should return 404 when food does not exist")
	void deleteFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/foods/999")).andExpectAll(status().isNotFound());
	}

	@FoodSql
	@Test
	@DisplayName("GET - /{id} - Should return food with image URLs, recipes, saves count, and saved=true when user has saved it")
	void getFood_ShouldReturnFoodWithSavedTrue_WhenUserHasSavedIt() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(get("/api/foods/1"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(1), jsonPath("$.name").value("Apple"),
					jsonPath("$.foodMacros.calories").value(95), jsonPath("$.images.imageUrls").isArray(),
					jsonPath("$.savesCount").value(2), jsonPath("$.saved").value(true), jsonPath("$.recipes").isArray(),
					jsonPath("$.recipes.length()").value(2), jsonPath("$.recipes[0].id").exists(),
					jsonPath("$.recipes[0].name").exists(), jsonPath("$.recipes[0].description").exists(),
					jsonPath("$.recipes[0].isPublic").exists(), jsonPath("$.recipes[0].author.username").exists(),
					jsonPath("$.recipes[0].author.id").exists());
	}

	@FoodSql
	@Test
	@DisplayName("GET - /{id} - Should return food with saved=false when user has not saved it")
	void getFood_ShouldReturnFoodWithSavedFalse_WhenUserHasNotSavedIt() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(get("/api/foods/2"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(2), jsonPath("$.name").value("Banana"),
					jsonPath("$.savesCount").value(1), jsonPath("$.saved").value(false));
	}

	@WithMockUser
	@FoodSql
	@Test
	@DisplayName("GET - /{id} - Should return 404 when food does not exist")
	void getFood_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/foods/999")).andExpectAll(status().isNotFound());
	}

	@WithMockUser
	@FoodSql
	@Test
	@DisplayName("POST - /{id}/calculate-macros - Should calculate food macros")
	void calculateFoodMacros_ShouldCalculateMacros_WhenRequestIsValid() throws Exception {
		Utils.setUserContext(1);
		CalculateFoodMacrosRequestDto request = new CalculateFoodMacrosRequestDto(BigDecimal.valueOf(100));

		mockMvc
			.perform(post("/api/foods/1/calculate-macros").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isOk(), jsonPath("$.foodMacros.calories").value(95));
	}

	@WithMockUser
	@Test
	@DisplayName("POST - /{id}/calculate-macros - Should return 404 when food does not exist")
	void calculateFoodMacros_ShouldReturn404_WhenFoodDoesNotExist() throws Exception {
		Utils.setUserContext(1);
		CalculateFoodMacrosRequestDto request = new CalculateFoodMacrosRequestDto(BigDecimal.valueOf(100));

		mockMvc
			.perform(post("/api/foods/999/calculate-macros").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isNotFound());
	}

}
