package source.code.integration.test.controller.workoutSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
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

import java.math.BigDecimal;

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

        var request = new WorkoutSetCreateDto(
                new BigDecimal("25.0"),
                new BigDecimal("12.0"),
                1,
                1
        );

        mockMvc.perform(post("/api/workout-sets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.weight").value(25.0),
                        jsonPath("$.repetitions").value(12.0),
                        jsonPath("$.workoutSetGroupId").value(1),
                        jsonPath("$.exerciseId").value(1)
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set when owner")
    void updateWorkoutSet() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "weight": 25.0,
                    "repetitions": 15.0
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing Workout Set when admin")
    void updateWorkoutSetAsAdmin() throws Exception {
        Utils.setAdminContext(2);

        int id = 1;
        var patch = """
                {
                    "weight": 30.0
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when not owner or admin")
    void updateWorkoutSetNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);

        int id = 1;
        var patch = """
                {
                    "weight": 25.0
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when Workout Set not found")
    void updateWorkoutSetNotFound() throws Exception {
        Utils.setUserContext(1);

        int id = 999;
        var patch = """
                {
                    "weight": 25.0
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNotFound());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 400 when invalid patch")
    void updateWorkoutSetInvalidPatch() throws Exception {
        Utils.setUserContext(1);

        int id = 1;
        var patch = """
                {
                    "weight": -1
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType("application/json")
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
                    "invalidField": "someValue"
                }
                """;

        mockMvc.perform(patch("/api/workout-sets/{id}", id)
                        .contentType("application/json")
                        .content(patch))
                .andExpectAll(status().isNoContent());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing Workout Set when owner")
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
    @DisplayName("DELETE - /{id} - Should delete an existing Workout Set when admin")
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
    @DisplayName("DELETE - /{id} - Should return 403 when not owner or admin")
    void deleteWorkoutSetNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);
        int id = 1;

        mockMvc.perform(delete("/api/workout-sets/{id}", id))
                .andExpectAll(status().isForbidden());
    }

    @WorkoutSetSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when Workout Set not found")
    void deleteWorkoutSetNotFound() throws Exception {
        Utils.setUserContext(1);
        int id = 999;

        mockMvc.perform(delete("/api/workout-sets/{id}", id))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSetSql
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return an existing Workout Set when Plan is public")
    void getWorkoutSetPublic() throws Exception {
        int id = 1;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.weight").value(20.0),
                        jsonPath("$.repetitions").value(10.0),
                        jsonPath("$.workoutSetGroupId").value(1),
                        jsonPath("$.exerciseId").value(1)
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
                        jsonPath("$.weight").value(20.0),
                        jsonPath("$.repetitions").value(10.0),
                        jsonPath("$.workoutSetGroupId").value(1),
                        jsonPath("$.exerciseId").value(1)
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
                        jsonPath("$.weight").value(20.0),
                        jsonPath("$.repetitions").value(10.0),
                        jsonPath("$.workoutSetGroupId").value(1),
                        jsonPath("$.exerciseId").value(1)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{workoutSetId} - Should return 404 when Workout Set not found")
    void getWorkoutSetNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/workout-sets/{id}", id))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workout-set-groups/{workoutSetGroupId} - Should return all Workout Sets for a Workout Set Group when Plan is public")
    void getAllWorkoutSetsForWorkoutSetGroup() throws Exception {
        int workoutSetGroupId = 1;

        mockMvc.perform(get("/api/workout-sets/workout-set-groups/{workoutSetGroupId}", workoutSetGroupId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].workoutSetGroupId").value(workoutSetGroupId),
                        jsonPath("$[0].weight").value(20.0),
                        jsonPath("$[0].repetitions").value(10.0),
                        jsonPath("$[0].exerciseId").value(1)
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workout-set-groups/{workoutSetGroupId} - Should return all Workout Sets for a Workout Set Group when owner")
    void getAllWorkoutSetsForWorkoutSetGroupOwner() throws Exception {
        Utils.setUserContext(1);
        int workoutSetGroupId = 1;

        mockMvc.perform(get("/api/workout-sets/workout-set-groups/{workoutSetGroupId}", workoutSetGroupId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].workoutSetGroupId").value(workoutSetGroupId),
                        jsonPath("$[0].weight").value(20.0),
                        jsonPath("$[0].repetitions").value(10.0),
                        jsonPath("$[0].exerciseId").value(1)
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workout-set-groups/{workoutSetGroupId} - Should return all Workout Sets for a Workout Set Group when admin")
    void getAllWorkoutSetsForWorkoutSetGroupAdmin() throws Exception {
        Utils.setAdminContext(2);
        int workoutSetGroupId = 1;

        mockMvc.perform(get("/api/workout-sets/workout-set-groups/{workoutSetGroupId}", workoutSetGroupId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].workoutSetGroupId").value(workoutSetGroupId),
                        jsonPath("$[0].weight").value(20.0),
                        jsonPath("$[0].repetitions").value(10.0),
                        jsonPath("$[0].exerciseId").value(1)
                );
    }

    @WorkoutSetSql
    @Test
    @DisplayName("GET - /workout-set-groups/{workoutSetGroupId} - Should return 403 when not owner or admin and plan is private")
    void getAllWorkoutSetsForWorkoutSetGroupForbidden() throws Exception {
        Utils.setUserContext(1);
        int workoutSetGroupId = 2;

        mockMvc.perform(get("/api/workout-sets/workout-set-groups/{workoutSetGroupId}", workoutSetGroupId))
                .andExpectAll(status().isForbidden());
    }


    @WithMockUser
    @Test
    @DisplayName("GET - /workout-set-groups/{workoutSetGroupId} - Should return return 404 when WorkoutSetGroup not found")
    void getAllWorkoutSetsForWorkoutSetGroupEmpty() throws Exception {
        int workoutSetGroupId = 999;

        mockMvc.perform(get("/api/workout-sets/workout-set-groups/{workoutSetGroupId}", workoutSetGroupId))
                .andExpectAll(status().isNotFound());
    }
}