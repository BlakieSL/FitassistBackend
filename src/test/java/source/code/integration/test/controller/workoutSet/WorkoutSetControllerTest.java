package source.code.integration.test.controller.workoutSet;

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
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class WorkoutSetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WorkoutSetSql
    @Test
    @DisplayName("POST - / - Should create a new Workout Set")
    void createWorkoutSet() throws Exception {
        Utils.setUserContext(1);

        var request = new WorkoutSetCreateDto(1, 60, 1);

        mockMvc.perform(post("/api/workout-sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.orderIndex").value(1),
                        jsonPath("$.restSeconds").value(60),
                        jsonPath("$.workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set")
    void updateWorkoutSet() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set whe admin")
    void updateWorkoutSetAsAdmin() throws Exception {
        Utils.setAdminContext(2);

        int id = 1;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when Workout Set does not exist")
    void updateWorkoutSetNotFound() throws Exception {
        Utils.setUserContext(1);

        int id = 999;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when user is not owner or admin")
    void updateWorkoutSetForbidden() throws Exception {
        Utils.setUserContext(3);

        int id = 1;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 400 when request body is invalid")
    void updateWorkoutSetBadRequest() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "restSeconds": -10
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isBadRequest());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should ignore invalid fields in patch")
    void updateWorkoutSetIgnoreInvalidFields() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "random": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("DELETE -/{id} - Should delete an existing Workout Set")
    void deleteWorkoutSet() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(delete("/api/workout-sets/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("DELETE -/{id} - Should delete an existing Workout Set when admin")
    void deleteWorkoutSetAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(delete("/api/workout-sets/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("DELETE -/{id} - Should return 404 when Workout Set does not exist")
    void deleteWorkoutSetNotFound() throws Exception {
        Utils.setUserContext(1);
        int id = 999;

        mockMvc.perform(delete("/api/workout-sets/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("DELETE -/{id} - Should return 403 when user is not owner or admin")
    void deleteWorkoutSetForbidden() throws Exception {
        Utils.setUserContext(3);
        int id = 1;

        mockMvc.perform(delete("/api/workout-sets/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @WorkoutSetSql
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return an existing Workout Set when public")
    void getWorkoutSetPublic() throws Exception {
        int id = 1;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.orderIndex").value(1),
                        jsonPath("$.restSeconds").value(60),
                        jsonPath("$.workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return an existing Workout Set when owner")
    void getWorkoutSet() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.orderIndex").value(1),
                        jsonPath("$.restSeconds").value(60),
                        jsonPath("$.workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return an existing Workout Set when admin")
    void getWorkoutSetAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.orderIndex").value(1),
                        jsonPath("$.restSeconds").value(60),
                        jsonPath("$.workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return 403 when user is not owner or admin and plan is private")
    void getWorkoutSetForbidden() throws Exception {
        Utils.setUserContext(1);
        int id = 2;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return 404 when Workout Set does not exist")
    void getWorkoutSetNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return all Workout Sets for a Workout when public")
    void getAllWorkoutSetsForWorkout() throws Exception {
        int workoutId = 1;

        mockMvc.perform(get("/api/workout-sets/workouts/{workoutId}", workoutId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].orderIndex").value(1),
                        jsonPath("$[0].restSeconds").value(60),
                        jsonPath("$[0].workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return all Workout Sets for a Workout when owner")
    void getAllWorkoutSetsForWorkoutOwner() throws Exception {
        Utils.setUserContext(2);
        int workoutId = 2;

        mockMvc.perform(get("/api/workout-sets/workouts/{workoutId}", workoutId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].orderIndex").value(1),
                        jsonPath("$[0].restSeconds").value(90),
                        jsonPath("$[0].workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return all Workout Sets for a Workout when admin")
    void getAllWorkoutSetsForWorkoutAdmin() throws Exception {
        Utils.setAdminContext(2);
        int workoutId = 2;

        mockMvc.perform(get("/api/workout-sets/workouts/{workoutId}", workoutId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].orderIndex").value(1),
                        jsonPath("$[0].restSeconds").value(90),
                        jsonPath("$[0].workoutSetExercises").isArray()
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return 403 when user is not owner or admin and plan is private")
    void getAllWorkoutSetsForWorkoutForbidden() throws Exception {
        Utils.setUserContext(1);
        int workoutId = 2;

        mockMvc.perform(get("/api/workout-sets/workouts/{workoutId}", workoutId))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return 404 when Workout not found")
    void getAllWorkoutSetsForWorkoutEmpty() throws Exception {
        int workoutId = 999;

        mockMvc.perform(get("/api/workout-sets/workouts/{workoutId}", workoutId))
                .andExpectAll(status().isNotFound());
    }
}
