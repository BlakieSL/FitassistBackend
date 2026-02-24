package com.fitassist.backend.integration.test.controller.daily;

import tools.jackson.databind.ObjectMapper;
import com.fitassist.backend.dto.request.activity.DailyActivityItemCreateDto;
import com.fitassist.backend.dto.request.activity.DailyActivityItemUpdateDto;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.test.controller.activity.ActivitySql;
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
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class DailyActivityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@ActivitySql
	@Test
	@DisplayName("GET - /{date} - Should return all daily activities for the user")
	void getAllDailyActivitiesByUser() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/daily-activities/2023-10-05"))
			.andExpectAll(status().isOk(), jsonPath("$.activities").isArray(),
					jsonPath("$.activities.length()").value(2), jsonPath("$.totalCaloriesBurned").isNumber());
	}

	@ActivitySql
	@Test
	@DisplayName("GET - /{date} - Should return empty list when no daily activity items exist in existing daily cart")
	void getAllDailyActivitiesByUserEmpty() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/daily-activities/2023-10-06"))
			.andExpectAll(status().isOk(), jsonPath("$.activities").isArray(),
					jsonPath("$.activities.length()").value(0), jsonPath("$.totalCaloriesBurned").value(0));
	}

	@ActivitySql
	@Test
	@DisplayName("GET - /{date} - Should return empty list when daily cart didn't exist for the date")
	void getAllDailyActivitiesByUserNotFound() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/daily-activities/2023-10-02"))
			.andExpectAll(status().isOk(), jsonPath("$.activities").isArray(),
					jsonPath("$.activities.length()").value(0), jsonPath("$.totalCaloriesBurned").value(0));
	}

	@ActivitySql
	@Test
	@DisplayName("POST - /add/{activityId} - Should add a daily activity to the user's cart")
	void addDailyActivityToUser() throws Exception {
		Utils.setUserContext(1);
		DailyActivityItemCreateDto request = new DailyActivityItemCreateDto((short) 30, BigDecimal.valueOf(75.5),
				LocalDate.of(2023, 10, 6));

		mockMvc
			.perform(post("/api/daily-activities/add/3").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());
	}

	@ActivitySql
	@Test
	@DisplayName("POST - /add/{activityId} - Should increase time if activity already exists")
	void addDailyActivityToUserAlreadyExists() throws Exception {
		Utils.setUserContext(1);
		DailyActivityItemCreateDto request = new DailyActivityItemCreateDto((short) 20, BigDecimal.valueOf(72.0),
				LocalDate.of(2023, 10, 5));

		mockMvc
			.perform(post("/api/daily-activities/add/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/api/daily-activities/2023-10-05"))
			.andExpectAll(jsonPath("$.activities[?(@.id == 1)].time").value(50),
					jsonPath("$.activities[?(@.id == 2)].time").value(45),
					jsonPath("$.activities[?(@.id == 1)].weight").value(72.0));
	}

	@ActivitySql
	@Test
	@DisplayName("POST - /add/{activityId} - Should return 404 when activity does not exist")
	void addDailyActivityToUserNotFound() throws Exception {
		Utils.setUserContext(1);
		DailyActivityItemCreateDto request = new DailyActivityItemCreateDto((short) 30, BigDecimal.valueOf(75.5),
				LocalDate.of(2023, 10, 6));

		mockMvc
			.perform(post("/api/daily-activities/add/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@ActivitySql
	@Test
	@DisplayName("PATCH - /modify-activity/{activityId} - Should update daily activity item")
	void updateDailyCartActivity() throws Exception {
		Utils.setUserContext(1);
		DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto((short) 40, BigDecimal.valueOf(80.0));

		mockMvc
			.perform(patch("/api/daily-activities/modify-activity/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/daily-activities/2023-10-05"))
			.andExpectAll(jsonPath("$.activities[?(@.id == 1)].time").value(40),
					jsonPath("$.activities[?(@.id == 1)].weight").value(80.0),
					jsonPath("$.activities[?(@.id == 2)].time").value(45));
	}

	@ActivitySql
	@Test
	@DisplayName("PATCH - /modify-activity/{activityId} - Should return 404 when daily activity item does not exist")
	void updateDailyCartActivityNotFound() throws Exception {
		Utils.setUserContext(1);
		DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto();

		mockMvc
			.perform(patch("/api/daily-activities/modify-activity/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());
	}

	@ActivitySql
	@Test
	@DisplayName("PATCH - /modify-activity/{activityId} - Should return 400 when patch is invalid")
	void updateDailyCartActivityInvalidPatch() throws Exception {
		Utils.setUserContext(1);
		DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto((short) -10, null);

		mockMvc
			.perform(patch("/api/daily-activities/modify-activity/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest());
	}

	@ActivitySql
	@Test
	@DisplayName("PATCH - /modify-activity/{activityId} - Should return 403 when user is not owner")
	void updateNotOwnerAdmin() throws Exception {
		Utils.setUserContext(2);
		DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto((short) 40, null);

		mockMvc
			.perform(patch("/api/daily-activities/modify-activity/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isForbidden());
	}

	@ActivitySql
	@Test
	@DisplayName("DELETE - /remove/{dailyActivityItemId} - Should delete")
	void removeActivityFromDailyCartActivity() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/daily-activities/remove/1")).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/daily-activities/2023-10-05"))
			.andExpectAll(jsonPath("$.activities.length()").value(1), jsonPath("$.activities[0].id").value(2));
	}

	@ActivitySql
	@Test
	@DisplayName("DELETE - /remove/{dailyActivityItemId} - Should return 404 when daily activity item does not exist")
	void removeActivityFromDailyCartActivityNotFound() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/daily-activities/remove/999")).andExpect(status().isNotFound());
	}

	@ActivitySql
	@Test
	@DisplayName("DELETE - /remove/{dailyActivityItemId} - Should return 403 when user is not owner")
	void removeNotOwnerAdmin() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/daily-activities/remove/1")).andExpect(status().isForbidden());
	}

	@ActivitySql
	@Test
	@DisplayName("POST - /add/{activityId} - Should return 400 when weight is not provided and user has no weight")
	void addDailyActivityNoWeightAvailable() throws Exception {
		Utils.setUserContext(2);
		DailyActivityItemCreateDto request = new DailyActivityItemCreateDto((short) 30, null,
				LocalDate.of(2023, 10, 6));

		mockMvc
			.perform(post("/api/daily-activities/add/3").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isBadRequest());
	}

}
