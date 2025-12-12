package source.code.integration.test.controller.workoutSetExercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class WorkoutSetExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("POST - / - Should create a new Workout Set Exercise")
    void createWorkoutSetExercise() throws Exception {
        Utils.setUserContext(1);

        var request = new WorkoutSetExerciseCreateDto(
                new BigDecimal("25.0"),
                (short) 12,
                1,
                1,
                (short) 1
        );

        mockMvc.perform(post("/api/workout-set-exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.weight").value(25.0),
                        jsonPath("$.repetitions").value(12),
                        jsonPath("$.orderIndex").exists(),
                        jsonPath("$.exerciseId").value(1),
                        jsonPath("$.exerciseName").exists()
                );
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set Exercise when owner")
    void updateWorkoutSetExercise() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "weight": 25.0,
                    "repetitions": 15.0
                }
                """;

        mockMvc.perform(patch("/api/workout-set-exercises/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set Exercise when admin")
    void updateWorkoutSetExerciseAsAdmin() throws Exception {
        Utils.setAdminContext(2);

        int id = 1;
        var patch = """
                {
                    "weight": 30.0
                }
                """;

        mockMvc.perform(patch("/api/workout-set-exercises/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when not owner or admin")
    void updateWorkoutSetExerciseNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);

        int id = 1;
        var patch = """
                {
                    "weight": 25.0
                }
                """;

        mockMvc.perform(patch("/api/workout-set-exercises/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when Workout Set Exercise not found")
    void updateWorkoutSetExerciseNotFound() throws Exception {
        Utils.setUserContext(1);

        int id = 999;
        var patch = """
                {
                    "weight": 25.0
                }
                """;

        mockMvc.perform(patch("/api/workout-set-exercises/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 400 when invalid patch")
    void updateWorkoutSetExerciseInvalidPatch() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "weight": -1
                }
                """;

        mockMvc.perform(patch("/api/workout-set-exercises/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isBadRequest());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should ignore invalid fields in patch")
    void updateWorkoutSetExerciseIgnoreInvalidFields() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "invalidField": "someValue"
                }
                """;

        mockMvc.perform(patch("/api/workout-set-exercises/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing Workout Set Exercise when owner")
    void deleteWorkoutSetExercise() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(delete("/api/workout-set-exercises/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workout-set-exercises/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing Workout Set Exercise when admin")
    void deleteWorkoutSetExerciseAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(delete("/api/workout-set-exercises/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workout-set-exercises/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 403 when not owner or admin")
    void deleteWorkoutSetExerciseNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);
        int id = 1;

        mockMvc.perform(delete("/api/workout-set-exercises/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when Workout Set Exercise not found")
    void deleteWorkoutSetExerciseNotFound() throws Exception {
        Utils.setUserContext(1);
        int id = 999;

        mockMvc.perform(delete("/api/workout-set-exercises/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /{workoutSetExerciseId} - Should return an existing Workout Set Exercise when Plan is public")
    void getWorkoutSetExercisePublic() throws Exception {
        int id = 1;

        mockMvc.perform(get("/api/workout-set-exercises/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.weight").value(20.0),
                        jsonPath("$.repetitions").value(10),
                        jsonPath("$.orderIndex").exists(),
                        jsonPath("$.exerciseId").value(1),
                        jsonPath("$.exerciseName").exists()
                );
    }
    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /{workoutSetExerciseId} - Should return an existing Workout Set Exercise when owner")
    void getWorkoutSetExercise() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(get("/api/workout-set-exercises/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.weight").value(20.0),
                        jsonPath("$.repetitions").value(10),
                        jsonPath("$.orderIndex").exists(),
                        jsonPath("$.exerciseId").value(1),
                        jsonPath("$.exerciseName").exists()
                );
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /{workoutSetExerciseId} - Should return an existing Workout Set Exercise when admin")
    void getWorkoutSetExerciseAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(get("/api/workout-set-exercises/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.weight").value(20.0),
                        jsonPath("$.repetitions").value(10),
                        jsonPath("$.orderIndex").exists(),
                        jsonPath("$.exerciseId").value(1),
                        jsonPath("$.exerciseName").exists()
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{workoutSetExerciseId} - Should return 404 when Workout Set Exercise not found")
    void getWorkoutSetExerciseNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/workout-set-exercises/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /workout-sets/{workoutSetId} - Should return all Workout Set Exercises for a Workout Set when Plan is public")
    void getAllWorkoutSetExercisesForWorkoutSet() throws Exception {
        int workoutSetId = 1;

        mockMvc.perform(get("/api/workout-set-exercises/workout-sets/{workoutSetId}", workoutSetId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].orderIndex").exists(),
                        jsonPath("$[0].weight").value(20.0),
                        jsonPath("$[0].repetitions").value(10),
                        jsonPath("$[0].exerciseId").value(1),
                        jsonPath("$[0].exerciseName").exists()
                );
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /workout-sets/{workoutSetId} - Should return all Workout Set Exercises for a Workout Set when owner")
    void getAllWorkoutSetExercisesForWorkoutSetOwner() throws Exception {
        Utils.setUserContext(1);
        int workoutSetId = 1;

        mockMvc.perform(get("/api/workout-set-exercises/workout-sets/{workoutSetId}", workoutSetId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].orderIndex").exists(),
                        jsonPath("$[0].weight").value(20.0),
                        jsonPath("$[0].repetitions").value(10),
                        jsonPath("$[0].exerciseId").value(1),
                        jsonPath("$[0].exerciseName").exists()
                );
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /workout-sets/{workoutSetId} - Should return all Workout Set Exercises for a Workout Set when admin")
    void getAllWorkoutSetExercisesForWorkoutSetAdmin() throws Exception {
        Utils.setAdminContext(2);
        int workoutSetId = 1;

        mockMvc.perform(get("/api/workout-set-exercises/workout-sets/{workoutSetId}", workoutSetId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].orderIndex").exists(),
                        jsonPath("$[0].weight").value(20.0),
                        jsonPath("$[0].repetitions").value(10),
                        jsonPath("$[0].exerciseId").value(1),
                        jsonPath("$[0].exerciseName").exists()
                );
    }

    @WorkoutSetExerciseSql
    @Test
    @DisplayName("GET - /workout-sets/{workoutSetId} - Should return 403 when not owner or admin and plan is private")
    void getAllWorkoutSetExercisesForWorkoutSetForbidden() throws Exception {
        Utils.setUserContext(1);
        int workoutSetId = 2;

        mockMvc.perform(get("/api/workout-set-exercises/workout-sets/{workoutSetId}", workoutSetId))
                .andExpectAll(status().isForbidden());
    }


    @WithMockUser
    @Test
    @DisplayName("GET - /workout-sets/{workoutSetId} - Should return return 404 when WorkoutSet not found")
    void getAllWorkoutSetExercisesForWorkoutSetEmpty() throws Exception {
        int workoutSetId = 999;

        mockMvc.perform(get("/api/workout-set-exercises/workout-sets/{workoutSetId}", workoutSetId))
                .andExpectAll(status().isNotFound());
    }
}
