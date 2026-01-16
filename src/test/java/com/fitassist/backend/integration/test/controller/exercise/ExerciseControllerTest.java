package com.fitassist.backend.integration.test.controller.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.exercise.ExerciseUpdateDto;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.integration.utils.Utils;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class ExerciseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@ExerciseSql
	@Test
	@DisplayName("POST - /api/exercises - Should create a new exercise")
	void createExercise() throws Exception {
		Utils.setAdminContext(1);

		ExerciseCreateDto request = new ExerciseCreateDto();
		request.setName("Test Exercise");
		request.setDescription("This is a test exercise.");
		request.setEquipmentId(1);
		request.setExpertiseLevelId(1);
		request.setMechanicsTypeId(1);
		request.setForceTypeId(1);
		request.setTargetMusclesIds(Collections.emptyList());
		request.setInstructions(Collections.emptyList());
		request.setTips(Collections.emptyList());

		mockMvc
			.perform(post("/api/exercises").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isCreated(), jsonPath("$.name").value("Test Exercise"),
					jsonPath("$.description").value("This is a test exercise."));
	}

	@Test
	@DisplayName("POST - /api/exercises - Non-admin user should get 403 Forbidden")
	void createExerciseAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		ExerciseCreateDto request = new ExerciseCreateDto();
		request.setName("Test Exercise");
		request.setDescription("This is a test exercise.");
		request.setEquipmentId(1);
		request.setExpertiseLevelId(1);
		request.setMechanicsTypeId(1);
		request.setForceTypeId(1);
		request.setTargetMusclesIds(Collections.emptyList());
		request.setInstructions(Collections.emptyList());
		request.setTips(Collections.emptyList());

		mockMvc
			.perform(post("/api/exercises").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden());
	}

	@ExerciseSql
	@Test
	@DisplayName("PATCH - /{id} - Should update an existing exercise")
	void updateExercise() throws Exception {
		Utils.setAdminContext(1);
		ExerciseUpdateDto updateDto = new ExerciseUpdateDto();
		updateDto.setName("Updated Exercise");
		updateDto.setDescription("This is an updated test exercise.");
		updateDto.setEquipmentId(2);

		mockMvc
			.perform(patch("/api/exercises/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/exercises/1").accept(MediaType.APPLICATION_JSON))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Updated Exercise"),
					jsonPath("$.description").value("This is an updated test exercise."),
					jsonPath("$.equipment.id").value(2));
	}

	@Test
	@DisplayName("PATCH - /{id} - Non-admin user should get 403 Forbidden")
	void updateExerciseAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		ExerciseUpdateDto updateDto = new ExerciseUpdateDto();
		updateDto.setName("Updated Exercise");
		updateDto.setDescription("This is an updated test exercise.");
		updateDto.setEquipmentId(2);

		mockMvc
			.perform(patch("/api/exercises/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH - /{id} - Should return 404 Not Found for non-existing exercise")
	void updateNonExistingExerciseShouldReturnNotFound() throws Exception {
		Utils.setAdminContext(1);
		ExerciseUpdateDto updateDto = new ExerciseUpdateDto();
		updateDto.setName("Updated Exercise");
		updateDto.setDescription("This is an updated test exercise.");
		updateDto.setEquipmentId(2);

		mockMvc
			.perform(patch("/api/exercises/999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());
	}

	@ExerciseSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete an existing exercise")
	void deleteExercise() throws Exception {
		Utils.setAdminContext(1);

		mockMvc.perform(delete("/api/exercises/5")).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/exercises/5")).andExpect(status().isNotFound());
	}

	@ExerciseSql
	@Test
	@DisplayName("DELETE - /{id} - Should return 500, when exercise is associated with a workout set")
	void deleteExerciseWithWorkoutSetShouldReturnInternalServerError() throws Exception {
		Utils.setAdminContext(1);

		mockMvc.perform(delete("/api/exercises/1")).andExpect(status().isInternalServerError());
	}

	@Test
	@DisplayName("DELETE - /{id} - Non-admin user should get 403 Forbidden")
	void deleteExerciseAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/exercises/1")).andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("DELETE - /{id} - Should return 404 Not Found for non-existing exercise")
	void deleteNonExistingExerciseShouldReturnNotFound() throws Exception {
		Utils.setAdminContext(1);

		mockMvc.perform(delete("/api/exercises/999")).andExpect(status().isNotFound());
	}

	@ExerciseSql
	@Test
	@DisplayName("GET - /{id} - Should retrieve an existing exercise with image URLs and plans")
	void getExercise() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/exercises/1").accept(MediaType.APPLICATION_JSON))
			.andExpectAll(status().isOk(), jsonPath("$.imageUrls").isArray(), jsonPath("$.plans").isArray(),
					jsonPath("$.plans.length()").value(2), jsonPath("$.plans[0].id").isNumber(),
					jsonPath("$.plans[0].name").isString(), jsonPath("$.plans[0].description").isString(),
					jsonPath("$.plans[0].isPublic").isBoolean(),
					jsonPath("$.plans[0].author.username").value("test_user"),
					jsonPath("$.plans[0].author.id").value(1), jsonPath("$.plans[0].likesCount").isNumber(),
					jsonPath("$.plans[0].savesCount").isNumber(), jsonPath("$.plans[0].views").isNumber(),
					jsonPath("$.plans[0].planStructureType").exists(),
					jsonPath("$.plans[0].planStructureType.value").isString(),
					jsonPath("$.plans[0].planStructureType.name").isString(), jsonPath("$.plans[0].createdAt").exists(),
					jsonPath("$.plans[0].interactedWithAt").doesNotExist());
	}

	@Test
	@DisplayName("GET - /{id} - Should return 404 Not Found for non-existing exercise")
	void getNonExistingExerciseShouldReturnNotFound() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/exercises/999").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}
