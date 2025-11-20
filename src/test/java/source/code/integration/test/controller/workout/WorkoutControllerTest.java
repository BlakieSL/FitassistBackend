package source.code.integration.test.controller.workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.workout.WorkoutCreateDto;
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
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WorkoutSql
    @Test
    @DisplayName("POST - / - Should create a new Workout")
    void createWorkout() throws Exception {
        Utils.setUserContext(1);

        var request = new WorkoutCreateDto(
                "Evening Workout",
                new BigDecimal("45.0"),
                1
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Evening Workout"),
                        jsonPath("$.duration").value(45.0),
                        jsonPath("$.planId").value(1)
                );
    }

    @WorkoutSql
    @Test
    @DisplayName("PATCH - /{id} - Should update Workout when owner")
    void updateWorkout() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "name": "Updated Morning Workout",
                    "duration": 40.0
                }
                """;

        mockMvc.perform(patch("/api/workouts/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSql
    @Test
    @DisplayName("PATCH - /{id} - Should update Workout when admin")
    void updateWorkoutAsAdmin() throws Exception {
        Utils.setAdminContext(2);

        int id = 1;
        var patch = """
                {
                    "duration": 35.0
                }
                """;

        mockMvc.perform(patch("/api/workouts/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when not owner or admin")
    void updateWorkoutForbidden() throws Exception {
        Utils.setUserContext(3);

        int id = 1;
        var patch = """
                {
                    "name": "Unauthorized Update"
                }
                """;

        mockMvc.perform(patch("/api/workouts/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when Workout not found")
    void updateWorkoutNotFound() throws Exception {
        Utils.setUserContext(1);

        int id = 999;
        var patch = """
                {
                    "name": "Non-existent Workout"
                }
                """;

        mockMvc.perform(patch("/api/workouts/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 400 when invalid patch")
    void updateWorkoutInvalidPatch() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var invalidPatch = """
                {
                    "duration": -10.0
                }
                """;

        mockMvc.perform(patch("/api/workouts/{id}", id)
                        .contentType("application/json")
                        .content(invalidPatch))
                .andExpectAll(status().isBadRequest());
    }

    @WorkoutSql
    @Test
    @DisplayName("PATCH - /{id} - Should ignore invalid fields in patch")
    void updateWorkoutIgnoreInvalidFields() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "name": "Valid Update",
                    "invalidField": "should be ignored",
                    "duration": 25.0
                }
                """;

        mockMvc.perform(patch("/api/workouts/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete Workout when owner")
    void deleteWorkout() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(delete("/api/workouts/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete Workout when admin")
    void deleteWorkoutAsAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(delete("/api/workouts/{id}", id))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WorkoutSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when Workout not found")
    void deleteWorkoutNotFound() throws Exception {
        Utils.setUserContext(1);
        int id = 999;

        mockMvc.perform(delete("/api/workouts/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 403 when user is not owner or admin")
    void deleteWorkoutForbidden() throws Exception {
        Utils.setUserContext(3);
        int id = 1;

        mockMvc.perform(delete("/api/workouts/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSql
    @Test
    @DisplayName("GET - /{id} - Should get Workout by ID when owner")
    void getWorkout() throws Exception {
        Utils.setUserContext(1);
        int id = 1;

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value("Morning Workout"),
                        jsonPath("$.duration").value(30.0),
                        jsonPath("$.planId").value(1)
                );
    }

    @WorkoutSql
    @Test
    @DisplayName("GET - /{id} - Should get Workout by ID when admin")
    void getWorkoutAdmin() throws Exception {
        Utils.setAdminContext(2);
        int id = 1;

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value("Morning Workout"),
                        jsonPath("$.duration").value(30.0),
                        jsonPath("$.planId").value(1)
                );
    }

    @WorkoutSql
    @Test
    @DisplayName("GET - /{id} - Should return 403 when user is not owner or admin and Plan is private")
    void getWorkoutForbidden() throws Exception {
        Utils.setUserContext(1);
        int id = 2;

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{id} - Should return 404 when Workout not found")
    void getWorkoutNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSql
    @Test
    @DisplayName("GET - /plans/{planId} - Should get all Workouts for a Plan when public")
    void getAllWorkoutsForPlan() throws Exception {
        int planId = 1;

        mockMvc.perform(get("/api/workouts/plans/{planId}", planId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Morning Workout"),
                        jsonPath("$[0].duration").value(30.0),
                        jsonPath("$[0].planId").value(planId)
                );
    }

    @WorkoutSql
    @Test
    @DisplayName("GET - /plans/{planId} - Should get all Workouts for a Plan when owner")
    void getAllWorkoutsForPlanOwner() throws Exception {
        Utils.setUserContext(1);
        int planId = 1;

        mockMvc.perform(get("/api/workouts/plans/{planId}", planId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Morning Workout"),
                        jsonPath("$[0].duration").value(30.0),
                        jsonPath("$[0].planId").value(planId)
                );
    }

    @WorkoutSql
    @Test
    @DisplayName("GET - /plans/{planId} - Should get all Workouts for a Plan when admin")
    void getAllWorkoutsForPlanAdmin() throws Exception {
        Utils.setAdminContext(2);
        int planId = 1;

        mockMvc.perform(get("/api/workouts/plans/{planId}", planId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Morning Workout"),
                        jsonPath("$[0].duration").value(30.0),
                        jsonPath("$[0].planId").value(planId)
                );
    }

    @WorkoutSql
    @Test
    @DisplayName("GET - /plans/{planId} - Should return 403 when user is not owner or admin and Plan is private")
    void getAllWorkoutsForPlanForbidden() throws Exception {
        Utils.setUserContext(1);
        int planId = 2;

        mockMvc.perform(get("/api/workouts/plans/{planId}", planId))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /plans/{planId} - Should return 404 when Plan not found")
    void getAllWorkoutsForPlanEmpty() throws Exception {
        int planId = 999;

        mockMvc.perform(get("/api/workouts/plans/{planId}", planId))
                .andExpectAll(status().isNotFound());
    }
}
