package source.code.integration.test.controller.workoutSetGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class WorkoutSetGroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WorkoutSetGroupSql
    @Test
    @DisplayName("POST - / - Should create a new Workout Set Group")
    void createWorkoutSetGroup() throws Exception {
        Utils.setUserContext(1);

        var request = new WorkoutSetGroupCreateDto(1, 60, 1);

        mockMvc.perform(post("/api/workout-set-groups")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.orderIndex").value(1),
                        jsonPath("$.restSeconds").value(60),
                        jsonPath("$.workoutId").value(1)
                );
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set Group")
    void updateWorkoutSetGroup() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-set-groups/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set Group whe admin")
    void updateWorkoutSetGroupAsAdmin() throws Exception {
        Utils.setAdminContext(2);

        int id = 1;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-set-groups/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when Workout Set Group does not exist")
    void updateWorkoutSetGroupNotFound() throws Exception {
        Utils.setUserContext(1);

        int id = 999;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-set-groups/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when user is not owner or admin")
    void updateWorkoutSetGroupForbidden() throws Exception {
        Utils.setUserContext(3);

        int id = 1;
        var patch = """
                {
                    "restSeconds": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-set-groups/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 400 when request body is invalid")
    void updateWorkoutSetGroupBadRequest() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "restSeconds": -10
                }
                """;

        mockMvc.perform(patch("/api/workout-set-groups/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isBadRequest());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("PATCH - /{id} - Should ignore invalid fields in patch")
    void updateWorkoutSetGroupIgnoreInvalidFields() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "random": 90
                }
                """;

        mockMvc.perform(patch("/api/workout-set-groups/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("DELETE -/{id} - Should delete an existing Workout Set Group")
    void deleteWorkoutSetGroup() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(delete("/api/workout-set-groups/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workout-set-groups/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("DELETE -/{id} - Should delete an existing Workout Set Group when admin")
    void deleteWorkoutSetGroupAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(delete("/api/workout-set-groups/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workout-set-groups/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("DELETE -/{id} - Should return 404 when Workout Set Group does not exist")
    void deleteWorkoutSetGroupNotFound() throws Exception {
        Utils.setUserContext(1);
        int id = 999;

        mockMvc.perform(delete("/api/workout-set-groups/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSetGroupSql
    @Test
    @DisplayName("DELETE -/{id} - Should return 403 when user is not owner or admin")
    void deleteWorkoutSetGroupForbidden() throws Exception {
        Utils.setUserContext(3);
        int id = 1;

        mockMvc.perform(delete("/api/workout-set-groups/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @WorkoutSetGroupSql
    @Test
    @DisplayName("GET - /{id} - Should return an existing Workout Set Group")
    void getWorkoutSetGroup() throws Exception {
        int id = 1;

        mockMvc.perform(get("/api/workout-set-groups/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.orderIndex").value(1),
                        jsonPath("$.restSeconds").value(60),
                        jsonPath("$.workoutId").value(1)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{id} - Should return 404 when Workout Set Group does not exist")
    void getWorkoutSetGroupNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/workout-set-groups/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSetGroupSql
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return all Workout Set Groups for a Workout")
    void getAllWorkoutSetGroupsForWorkout() throws Exception {
        int workoutId = 1;

        mockMvc.perform(get("/api/workout-set-groups/workouts/{workoutId}", workoutId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].workoutId").value(workoutId),
                        jsonPath("$[0].orderIndex").value(1),
                        jsonPath("$[0].restSeconds").value(60)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /workouts/{workoutId} - Should return empty list when no Workout Set Groups exist for Workout")
    void getAllWorkoutSetGroupsForWorkoutEmpty() throws Exception {
        int workoutId = 999;

        mockMvc.perform(get("/api/workout-set-groups/workouts/{workoutId}", workoutId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$").isEmpty()
                );
    }
}