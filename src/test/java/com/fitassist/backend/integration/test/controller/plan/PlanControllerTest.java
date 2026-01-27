package com.fitassist.backend.integration.test.controller.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.request.plan.PlanUpdateDto;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.integration.utils.Utils;
import com.fitassist.backend.model.plan.PlanStructureType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class PlanControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@PlanSql
	@Test
	@DisplayName("POST - / - Should create a new plan")
	void createPlan() throws Exception {
		Utils.setUserContext(1);
		PlanCreateDto createDto = new PlanCreateDto();
		createDto.setName("Test Plan");
		createDto.setDescription("A test plan description");
		createDto.setPlanStructureType(PlanStructureType.WEEKLY_SPLIT);
		createDto.setCategoryIds(List.of(1));

		mockMvc
			.perform(post("/api/plans").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpectAll(status().isCreated(), jsonPath("$.id").exists(), jsonPath("$.name").value("Test Plan"),
					jsonPath("$.description").value("A test plan description"));
	}

	@PlanSql
	@Test
	@DisplayName("PATCH - /{id} - Should update an existing plan when owner")
	void updatePlan() throws Exception {
		Utils.setUserContext(1);
		PlanUpdateDto updateDto = new PlanUpdateDto();
		updateDto.setName("Updated Plan");

		mockMvc
			.perform(patch("/api/plans/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/plans/1").accept(MediaType.APPLICATION_JSON))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Updated Plan"));
	}

	@PlanSql
	@Test
	@DisplayName("PATCH - /{id} - Should update an existing plan when admin")
	void updatePlanAsAdmin() throws Exception {
		Utils.setAdminContext(5);
		PlanUpdateDto updateDto = new PlanUpdateDto();
		updateDto.setName("Updated Plan");

		mockMvc
			.perform(patch("/api/plans/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/plans/1").accept(MediaType.APPLICATION_JSON))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Updated Plan"));
	}

	@PlanSql
	@Test
	@DisplayName("PATCH - /{id} - Should return 403 when not owner or admin")
	void updatePlanForbidden() throws Exception {
		Utils.setUserContext(2);
		PlanUpdateDto updateDto = new PlanUpdateDto();
		updateDto.setName("Updated Plan");

		mockMvc
			.perform(patch("/api/plans/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH - /{id} - Should return 404 when plan not found")
	void updatePlanNotFound() throws Exception {
		Utils.setUserContext(1);
		PlanUpdateDto updateDto = new PlanUpdateDto();
		updateDto.setName("Updated Plan");

		mockMvc
			.perform(patch("/api/plans/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());
	}

	@PlanSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete an existing plan when owner")
	void deletePlan() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/plans/1")).andExpect(status().isNoContent());
	}

	@PlanSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete an existing plan when admin")
	void deletePlanAsAdmin() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/plans/1")).andExpect(status().isNoContent());
	}

	@PlanSql
	@Test
	@DisplayName("DELETE - /{id} - Should return 403 when not owner or admin")
	void deletePlanForbidden() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/plans/1")).andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("DELETE - /{id} - Should return 404 when plan not found")
	void deletePlanNotFound() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/plans/999")).andExpect(status().isNotFound());
	}

	@PlanSql
	@Test
	@DisplayName("GET - /{id} - Should retrieve an existing plan with all fields")
	void getPlan() throws Exception {
		Utils.setUserContext(2);

		mockMvc.perform(get("/api/plans/1"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(1), jsonPath("$.name").value("Beginner Strength"),
					jsonPath("$.description").value("Beginner workout plan"), jsonPath("$.isPublic").value(true),
					jsonPath("$.createdAt").exists(), jsonPath("$.views").value(0), jsonPath("$.author.id").value(1),
					jsonPath("$.author.username").value("fitness_lover"),
					jsonPath("$.author.imageName").value("user1_profile.jpg"), jsonPath("$.author.imageUrl").exists(),
					jsonPath("$.likesCount").value(1), jsonPath("$.dislikesCount").value(1),
					jsonPath("$.savesCount").value(0), jsonPath("$.liked").value(true),
					jsonPath("$.disliked").value(false), jsonPath("$.saved").value(false),
					jsonPath("$.planStructureType.value").value("WEEKLY_SPLIT"),
					jsonPath("$.planStructureType.name").value("Weekly Split"), jsonPath("$.categories", hasSize(2)),
					jsonPath("$.instructions", hasSize(2)), jsonPath("$.instructions[0].orderIndex").value(1),
					jsonPath("$.instructions[0].text").value("Warm up for 10 minutes before starting"),
					jsonPath("$.instructions[1].orderIndex").value(2), jsonPath("$.imageUrls", hasSize(2)),
					jsonPath("$.workouts", hasSize(2)), jsonPath("$.workouts[0].name").value("Upper Body"),
					jsonPath("$.workouts[0].duration").value(60.0), jsonPath("$.workouts[0].workoutSets", hasSize(2)),
					jsonPath("$.workouts[0].workoutSets[0].orderIndex").value(1),
					jsonPath("$.workouts[0].workoutSets[0].restSeconds").value(60),
					jsonPath("$.workouts[0].workoutSets[0].workoutSetExercises", hasSize(3)),
					jsonPath("$.workouts[0].workoutSets[0].workoutSetExercises[0].exerciseName")
						.value("Barbell Bench Press"),
					jsonPath("$.workouts[0].workoutSets[0].workoutSetExercises[0].weight").value(135.0),
					jsonPath("$.workouts[0].workoutSets[0].workoutSetExercises[0].repetitions").value(10));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /{id} - Should return 404 when plan not found")
	void getPlanNotFound() throws Exception {
		mockMvc.perform(get("/api/plans/999").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@PlanSql
	@Test
	@DisplayName("GET - /{id} - Should return 403 when not owner or admin and plan is private")
	void getPrivatePlanForbidden() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(get("/api/plans/5").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@WithMockUser
	@PlanSql
	@Test
	@DisplayName("PATCH - /{id}/view - Should increment views for a plan")
	void incrementViews() throws Exception {
		mockMvc.perform(patch("/api/plans/1/view")).andExpect(status().isNoContent());
	}

	@WithMockUser
	@PlanSql
	@Test
	@DisplayName("PATCH - /{id}/view - Should increment views multiple times")
	void incrementViewsMultipleTimes() throws Exception {
		mockMvc.perform(patch("/api/plans/1/view")).andExpect(status().isNoContent());
		mockMvc.perform(patch("/api/plans/1/view")).andExpect(status().isNoContent());
		mockMvc.perform(patch("/api/plans/1/view")).andExpect(status().isNoContent());
	}

	@WithMockUser
	@Test
	@DisplayName("PATCH - /{id}/view - Should return 204 even for non-existent plan")
	void incrementViewsNonExistentPlan() throws Exception {
		mockMvc.perform(patch("/api/plans/999/view")).andExpect(status().isNoContent());
	}

}
