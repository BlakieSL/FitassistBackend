package source.code.integration.test.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
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
public class UserSavedControllerWithoutTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType} - Should return all saved items of a specific type")
    void getAllFromUser() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(get("/api/user-saved/item-type/ACTIVITY"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(hasSize(2))
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/{itemId}/likes-ans-saves - Should return likes and saves for an item")
    void calculateLikesAndSaves() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/EXERCISE/1/likes-and-saves"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.likes").value(0),
                        jsonPath("$.saves").value(2)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/{itemId}/likes-ans-saves - Should return zero likes and saves for an item without likes and saves")
    void calculateLikesAndSavesEmpty() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/FORUM_THREAD/2/likes-and-saves"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.likes").value(0),
                        jsonPath("$.saves").value(0)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /item-type/{itemType}/{itemId}/likes-ans-saves - Should return 404 for non-existing item")
    void calculateLikesAndSavesNotFound() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/EXERCISE/999/likes-and-saves"))
                .andExpectAll(status().isNotFound());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId} - Should save an item to user")
    void saveToUser() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/EXERCISE/2"))
                .andExpectAll(status().isCreated());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId} - Should return 409 when item already saved")
    void saveToUserAlreadySaved() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/EXERCISE/1"))
                .andExpectAll(status().isConflict());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}- Should return 404 when item not found")
    void saveToUserNotFound() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/EXERCISE/999"))
                .andExpectAll(status().isNotFound());
    }

    @UserSavedSql
    @Test
    @DisplayName("DELETE - /item-type/{itemType}/{itemId} - Should delete an item from user")
    void deleteFromUser() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/user-saved/item-type/EXERCISE/1"))
                .andExpectAll(status().isNoContent());
    }

    @UserSavedSql
    @Test
    @DisplayName("DELETE - /item-type/{itemType}/{itemId} - Should return 404 when item not found")
    void deleteFromUserNotFound() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/user-saved/item-type/EXERCISE/999"))
                .andExpectAll(status().isNotFound());
    }
}
