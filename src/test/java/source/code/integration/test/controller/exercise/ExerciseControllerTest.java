package source.code.integration.test.controller.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    @DisplayName("POST - / - Should create a new exercise")
    void createExercise() throws Exception {
        Utils.setAdminContext(1);
    }

    @Test
    @DisplayName("POST - / - Non-admin user should get 403 Forbidden")
    void createExerciseAsUserShouldForbid() throws Exception {
        Utils.setUserContext(1);;
    }

    @ExerciseSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing exercise")
    void updateExercise() throws Exception {
        Utils.setAdminContext(1);
        // Implement the test logic for updating an exercise
    }

    @Test
    @DisplayName("PATCH - /{id} - Non-admin user should get 403 Forbidden")
    void updateExerciseAsUserShouldForbid() throws Exception {
        Utils.setUserContext(1);
        // Implement the test logic for a non-admin user trying to update an exercise
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 404 Not Found for non-existing exercise")
    void updateNonExistingExerciseShouldReturnNotFound() throws Exception {
        Utils.setAdminContext(1);
        // Implement the test logic for updating a non-existing exercise
    }

    @ExerciseSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing exercise")
    void deleteExercise() throws Exception {
        Utils.setAdminContext(1);
        // Implement the test logic for deleting an exercise
    }

    @Test
    @DisplayName("DELETE - /{id} - Non-admin user should get 403 Forbidden")
    void deleteExerciseAsUserShouldForbid() throws Exception {
        Utils.setUserContext(1);
        // Implement the test logic for a non-admin user trying to delete an exercise
    }

    @Test
    @DisplayName("DELETE - /{id} - Should return 404 Not Found for non-existing exercise")
    void deleteNonExistingExerciseShouldReturnNotFound() throws Exception {
        Utils.setAdminContext(1);
        // Implement the test logic for deleting a non-existing exercise
    }

    @ExerciseSql
    @Test
    @DisplayName("GET - /{id} - Should retrieve an existing exercise")
    void getExercise() throws Exception {
        Utils.setUserContext(1);
        // Implement the test logic for retrieving an exercise
    }

    @Test
    @DisplayName("GET - /{id} - Should return 404 Not Found for non-existing exercise")
    void getNonExistingExerciseShouldReturnNotFound() throws Exception {
        Utils.setUserContext(1);
        // Implement the test logic for retrieving a non-existing exercise
    }

    @ExerciseSql
    @Test
    @DisplayName("GET - / - Should retrieve all exercises")
    void getAllExercises() throws Exception {
        Utils.setUserContext(1);
        // Implement the test logic for retrieving all exercises
    }

    @ExerciseSql
    @Test
    @DisplayName("GET - /{categoryId}/categories - Should retrieve exercises by category")
    void getExercisesByCategory() throws Exception {
        Utils.setUserContext(1);
        // Implement the test logic for retrieving exercises by category
    }
}
