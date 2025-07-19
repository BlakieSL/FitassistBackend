package source.code.integration.test.controller.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.exercise.ExerciseCreateDto;
import source.code.dto.request.text.ExerciseInstructionCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=exercise")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
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

        mockMvc.perform(post("/api/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value("Test Exercise"),
                        jsonPath("$.description").value("This is a test exercise.")
                );

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

        mockMvc.perform(post("/api/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @ExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing exercise")
    void updateExercise() throws Exception {
        Utils.setAdminContext(1);

        String jsonPatch = """
                {
                    "name": "Updated Exercise",
                    "description": "This is an updated test exercise.",
                    "equipmentId": 2
                }
                """;

        mockMvc.perform(patch("/api/exercises/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPatch))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/exercises/1")
        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("Updated Exercise"),
                        jsonPath("$.description").value("This is an updated test exercise."),
                        jsonPath("$.equipment.id").value(2)
                );
    }

    @Test
    @DisplayName("PATCH - /{id} - Non-admin user should get 403 Forbidden")
    void updateExerciseAsUserShouldForbid() throws Exception {
        Utils.setUserContext(1);

        String jsonPatch = """
                {
                    "name": "Updated Exercise",
                    "description": "This is an updated test exercise.",
                    "equipmentId": 2
                }
                """;

        mockMvc.perform(patch("/api/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatch))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 404 Not Found for non-existing exercise")
    void updateNonExistingExerciseShouldReturnNotFound() throws Exception {
        Utils.setAdminContext(1);
        String jsonPatch = """
                {
                    "name": "Updated Exercise",
                    "description": "This is an updated test exercise.",
                    "equipmentId": 2
                }
                """;

        mockMvc.perform(patch("/api/exercises/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatch))
                .andExpect(status().isNotFound());
    }

    @ExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing exercise")
    void deleteExercise() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/exercises/5"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/exercises/5"))
                .andExpect(status().isNotFound());
    }

    @ExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 500, when exercise is associated with a workout set")
    void deleteExerciseWithWorkoutSetShouldReturnInternalServerError() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/exercises/1"))
                .andExpect(status().isInternalServerError());

    }

    @Test
    @DisplayName("DELETE - /{id} - Non-admin user should get 403 Forbidden")
    void deleteExerciseAsUserShouldForbid() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/exercises/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE - /{id} - Should return 404 Not Found for non-existing exercise")
    void deleteNonExistingExerciseShouldReturnNotFound() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/exercises/999"))
                .andExpect(status().isNotFound());
    }

    @ExerciseSql
    @Test
    @DisplayName("GET - /{id} - Should retrieve an existing exercise")
    void getExercise() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/exercises/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @DisplayName("GET - /{id} - Should return 404 Not Found for non-existing exercise")
    void getNonExistingExerciseShouldReturnNotFound() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/exercises/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @ExerciseSql
    @Test
    @DisplayName("GET - / - Should retrieve all exercises")
    void getAllExercises() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/exercises")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk());
    }

    @ExerciseSql
    @Test
    @DisplayName("GET - /{categoryId}/categories - Should retrieve exercises by category")
    void getExercisesByCategory() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/exercises/1/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk());
    }
}
