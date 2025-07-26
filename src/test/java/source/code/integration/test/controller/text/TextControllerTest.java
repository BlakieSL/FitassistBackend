package source.code.integration.test.controller.text;

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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class TextControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return all EXERCISE_INSTRUCTION text when admin")
    void getAllTextForExerciseInstruction() throws Exception {
        Utils.setAdminContext(1);

        int parentId = 1;
        String type = "EXERCISE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(3))
                );
    }

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return 403 when not admin fetching EXERCISE_INSTRUCTION text")
    void getAllTextForExerciseInstructionForbidden() throws Exception {
        Utils.setUserContext(1);

        int parentId = 1;
        String type = "EXERCISE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(status().isForbidden());
    }

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return all RECIPE_INSTRUCTION text when owner")
    void getAllTextForRecipeInstruction() throws Exception  {
        Utils.setUserContext(1);

        int parentId = 2;
        String type = "RECIPE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );
    }

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return all RECIPE_INSTRUCTION text when admin")
    void getAllTextForRecipeInstructionAdmin() throws Exception {
        Utils.setAdminContext(1);

        int parentId = 2;
        String type = "RECIPE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );
    }

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return 403 when not public, not owner, not admin")
    void getAllTextForRecipeInstructionForbidden() throws Exception {
        Utils.setUserContext(2);

        int parentId = 2;
        String type = "RECIPE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(status().isForbidden());
    }

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return 404 when parent item doesn't exist for RECIPE_INSTRUCTION or PLAN_INSTRUCTION")
    void getAllTextForParentNotFound() throws Exception {
        Utils.setUserContext(2);
        int parentId = 999;
        String type = "RECIPE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(status().isNotFound());
    }

    @TextSql
    @Test
    @DisplayName("GET - /{parentId}/type/{type} - Should return 403 when parent item doesn't exist for EXERCISE")
    void getAllTextForExerciseInstructionNotFound() throws Exception {
        Utils.setUserContext(2);
        int parentId = 999;
        String type = "EXERCISE_INSTRUCTION";

        mockMvc.perform(get("/api/text/{parentId}/type/{type}", parentId, type))
                .andExpectAll(status().isForbidden());
    }

    @TextSql
    @Test
    @DisplayName("PATCH - /{id}/type/{type} - Should update EXERCISE_INSTRUCTION text when admin")
    void updateExerciseInstructionText() throws Exception {
        Utils.setAdminContext(1);

        int id = 1;
        String type = "EXERCISE_INSTRUCTION";

        String request = """
                {
                    "text": "Updated text"
                }
                """;

        mockMvc.perform(patch("/api/text/{id}/type/{type}", id, type)
                        .contentType("application/json")
                        .content(request)
                ).andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/text/{id}/type/{type}", id, type))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].text").value("Updated text")
                );
    }

    @TextSql
    @Test
    @DisplayName("PATCH - /{id}/tpe/{type} - Should update RECIPE_INSTRUCTION text when owner")
    void updateInstructionText() throws Exception {
        Utils.setUserContext(1);

        int id = 4;
        String type = "RECIPE_INSTRUCTION";

        String request = """
                {
                    "text": "Updated text"
                }
                """;

        mockMvc.perform(patch("/api/text/{id}/type/{type}", id, type)
                .contentType("application/json")
                .content(request)
        ).andExpectAll(status().isNoContent());
    }

    @TextSql
    @Test
    @DisplayName("PATCH - /{id}/tpe/{type} - Should return 403 when not owner nor admin")
    void updateTextForbidden() throws Exception {
        Utils.setUserContext(2);

        int id = 1;
        String type = "EXERCISE_INSTRUCTION";

        String request = """
                {
                    "text": "Updated text"
                }
                """;

        mockMvc.perform(patch("/api/text/{id}/type/{type}", id, type)
                        .contentType("application/json")
                        .content(request)
                ).andExpectAll(status().isForbidden());
    }

    @TextSql
    @Test
    @DisplayName("PATCH - /{id}/tpe/{type} - Should return 404 when text not found")
    void updateTextNotFound() throws Exception {
        Utils.setAdminContext(1);

        int id = 999;
        String type = "EXERCISE_INSTRUCTION";

        String request = """
                {
                    "text": "Updated text"
                }
                """;

        mockMvc.perform(patch("/api/text/{id}/type/{type}", id, type)
                        .contentType("application/json")
                        .content(request)
                ).andExpectAll(status().isNotFound());
    }

    @TextSql
    @Test
    @DisplayName("DELETE - /{id}/type/{type} - Should delete EXERCISE_INSTRUCTION text when admin")
    void deleteExerciseInstructionText() throws Exception {
        Utils.setAdminContext(1);

        int id = 1;
        String type = "EXERCISE_INSTRUCTION";

        mockMvc.perform(delete("/api/text/{id}/type/{type}", id, type))
                .andExpectAll(status().isNoContent());
    }

    @TextSql
    @Test
    @DisplayName("DELETE - /{id}/type/{type} - Should delete RECIPE_INSTRUCTION text when owner")
    void deleteRecipeInstructionText() throws Exception {
        Utils.setUserContext(1);

        int id = 4;
        String type = "RECIPE_INSTRUCTION";

        mockMvc.perform(delete("/api/text/{id}/type/{type}", id, type))
                .andExpectAll(status().isNoContent());
    }

    @TextSql
    @Test
    @DisplayName("DELETE - /{id}/type/{type} - Should return 403 when not owner nor admin")
    void deleteTextForbidden() throws Exception {
        Utils.setUserContext(2);

        int id = 1;
        String type = "EXERCISE_INSTRUCTION";

        mockMvc.perform(delete("/api/text/{id}/type/{type}", id, type))
                .andExpectAll(status().isForbidden());
    }

    @TextSql
    @Test
    @DisplayName("DELETE - /{id}/type/{type} - Should return 404 when text not found")
    void deleteTextNotFound() throws Exception {
        Utils.setAdminContext(1);

        int id = 999;
        String type = "EXERCISE_INSTRUCTION";

        mockMvc.perform(delete("/api/text/{id}/type/{type}", id, type))
                .andExpectAll(status().isNotFound());
    }
}
