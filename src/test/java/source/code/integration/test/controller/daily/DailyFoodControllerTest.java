package source.code.integration.test.controller.daily;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.food.DailyCartFoodCreateDto;
import source.code.dto.request.food.DailyCartFoodGetDto;
import source.code.dto.request.food.DailyCartFoodUpdateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.test.controller.food.FoodSql;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class DailyFoodControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@FoodSql
	@Test
	@DisplayName("POST - / - Should return all daily foods for the user")
	void getAllDailyFoodsByUser() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodGetDto request = new DailyCartFoodGetDto();
		request.setDate(LocalDate.of(2025, 4, 1));

		mockMvc
			.perform(post("/api/cart").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isOk(), jsonPath("$.foods").isArray(), jsonPath("$.foods.length()").value(3),
					jsonPath("$.totalCalories").isNumber());
	}

	@Test
	@DisplayName("POST - / - Should return empty list when no daily food items exist in existing daily cart")
	void getAllDailyFoodsByUserEmpty() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodGetDto request = new DailyCartFoodGetDto();
		request.setDate(LocalDate.of(2025, 4, 4));

		mockMvc
			.perform(post("/api/cart").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isOk(), jsonPath("$.foods").isArray(), jsonPath("$.foods.length()").value(0),
					jsonPath("$.totalCalories").value(0));
	}

	@Test
	@DisplayName("POST - / - Should return empty list when daily cart didn't exist for the date")
	void getAllDailyFoodsByUserNotFound() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodGetDto request = new DailyCartFoodGetDto();
		request.setDate(LocalDate.of(2023, 10, 2));

		mockMvc
			.perform(post("/api/cart").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isOk(), jsonPath("$.foods").isArray(), jsonPath("$.foods.length()").value(0),
					jsonPath("$.totalCalories").value(0));
	}

	@FoodSql
	@Test
	@DisplayName("POST - /add/{foodId} - Should add a daily food to the user's cart")
	void addDailyFoodToUser() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodCreateDto request = new DailyCartFoodCreateDto();
		request.setQuantity(BigDecimal.valueOf(2.5));
		request.setDate(LocalDate.of(2025, 4, 4));

		mockMvc
			.perform(post("/api/cart/add/4").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		DailyCartFoodGetDto verifyRequest = new DailyCartFoodGetDto();
		verifyRequest.setDate(LocalDate.of(2025, 4, 4));

		mockMvc
			.perform(post("/api/cart").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifyRequest)))
			.andExpectAll(jsonPath("$.foods.length()").value(1), jsonPath("$.foods[0].id").value(4),
					jsonPath("$.foods[0].quantity").value(2.5));
	}

	@FoodSql
	@Test
	@DisplayName("POST - /add/{foodId} - Should increase quantity if food already exists")
	void addDailyFoodToUserAlreadyExists() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodCreateDto request = new DailyCartFoodCreateDto();
		request.setQuantity(BigDecimal.valueOf(1.0));
		request.setDate(LocalDate.of(2025, 4, 1));

		mockMvc
			.perform(post("/api/cart/add/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		DailyCartFoodGetDto verifyRequest = new DailyCartFoodGetDto();
		verifyRequest.setDate(LocalDate.of(2025, 4, 1));

		mockMvc
			.perform(post("/api/cart").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifyRequest)))
			.andExpectAll(jsonPath("$.foods[?(@.id == 1)].quantity").value(3.0),
					jsonPath("$.foods[?(@.id == 3)].quantity").value(1.0),
					jsonPath("$.foods[?(@.id == 5)].quantity").value(1.5));
	}

	@Test
	@DisplayName("POST - /add/{foodId} - Should return 404 when food does not exist")
	void addDailyFoodToUserNotFound() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodCreateDto request = new DailyCartFoodCreateDto();
		request.setQuantity(BigDecimal.valueOf(1.0));
		request.setDate(LocalDate.of(2025, 4, 4));

		mockMvc
			.perform(post("/api/cart/add/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@FoodSql
	@Test
	@DisplayName("DELETE - /remove/{dailyCartFoodId} - Should remove a food item from the daily cart")
	void removeFoodFromDailyCart() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/cart/remove/1")).andExpect(status().isNoContent());

		DailyCartFoodGetDto request = new DailyCartFoodGetDto();
		request.setDate(LocalDate.of(2025, 4, 1));

		mockMvc
			.perform(post("/api/cart").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(jsonPath("$.foods").isArray(), jsonPath("$.foods.length()").value(2),
					jsonPath("$.foods[?(@.id == 3)].quantity").value(1.0),
					jsonPath("$.foods[?(@.id == 5)].quantity").value(1.5));
	}

	@Test
	@DisplayName("DELETE - /remove/{dailyCartFoodId} - Should return 404 when food item does not exist")
	void removeFoodFromDailyCartNotFound() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/cart/remove/999")).andExpect(status().isNotFound());
	}

	@FoodSql
	@Test
	@DisplayName("DELETE - /remove/{dailyCartFoodId} - Should return 403 when not owner")
	void removeFoodFromDailyCartForbidden() throws Exception {
		Utils.setUserContext(2);

		mockMvc.perform(delete("/api/cart/remove/1")).andExpect(status().isForbidden());
	}

	@FoodSql
	@Test
	@DisplayName("PATCH - /update/{dailyCartFoodId} - Should update a food item in the daily cart")
	void updateDailyCartFood() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodUpdateDto updateDto = DailyCartFoodUpdateDto.of(BigDecimal.valueOf(3.0));

		mockMvc
			.perform(patch("/api/cart/update/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("PATCH - /update/{dailyCartFoodId} - Should return 404 when food item does not exist")
	void updateDailyCartFoodNotFound() throws Exception {
		Utils.setUserContext(1);

		DailyCartFoodUpdateDto updateDto = new DailyCartFoodUpdateDto();

		mockMvc
			.perform(patch("/api/cart/update/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());
	}

	@FoodSql
	@Test
	@DisplayName("PATCH - /update/{dailyCartFoodId} - Should return 403 when not owner")
	void updateDailyCartFoodForbidden() throws Exception {
		Utils.setUserContext(2);

		DailyCartFoodUpdateDto updateDto = DailyCartFoodUpdateDto.of(BigDecimal.valueOf(3.0));

		mockMvc
			.perform(patch("/api/cart/update/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isForbidden());
	}

}
