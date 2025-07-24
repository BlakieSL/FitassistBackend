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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class UserSavedControllerWithTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type} - Should return all saved items of a specific type SAVE")
    void getAllFromUserSave() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/type/SAVE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(hasSize(1))
                );
    }

    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type} - Should return all saved items of a specific type LIKE")
    void getAllFromUserLike() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-saved/item-type/COMMENT/type/LIKE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1))
                );

    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/{itemId}/likes-ans-saves - Should return likes and saves for an item")
    void calculateLikesAndSaves() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/COMMENT/1/likes-ans-saves"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.likes").value(1),
                        jsonPath("$.saves").value(0)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/{itemId}/likes-ans-saves - Should return zero likes and saves for an item without likes and saves")
    void calculateLikesAndSavesEmpty() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/3/likes-ans-saves"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.likes").value(0),
                        jsonPath("$.saves").value(0)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/{itemId}/likes-ans-saves - Should return 404 for non-existing item")
    void calculateLikesAndSavesNotFound() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/999/likes-ans-saves"))
                .andExpectAll(status().isNotFound());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should SAVE an item to user")
    void saveToUserSave() throws Exception {
        Utils.setUserContext(3);

        mockMvc.perform(post("/api/user-saved/item-type/PLAN/3/type/SAVE"))
                .andExpectAll(status().isCreated());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should LIKE an item to user")
    void saveToUserLike() throws Exception {
        Utils.setUserContext(3);

        mockMvc.perform(post("/api/user-saved/item-type/PLAN/3/type/LIKE"))
                .andExpectAll(status().isCreated());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should return 409 when item already SAVED")
    void saveToUserAlreadySaved() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/PLAN/1/type/SAVE"))
                .andExpectAll(status().isConflict());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should return 409 when item already LIKED")
    void saveToUserAlreadyLiked() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/COMMENT/3/type/LIKE"))
                .andExpectAll(status().isConflict());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should save when item already liked but not saved")
    void saveToUserLikeNotSaved() throws Exception {
        Utils.setUserContext(2);

        mockMvc.perform(post("/api/user-saved/item-type/COMMENT/1/type/SAVE"))
                .andExpectAll(status().isCreated());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should like when item already saved but not liked")
    void saveToUserSaveNotLiked() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/PLAN/1/type/LIKE"))
                .andExpectAll(status().isCreated());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should return 404 when item not found")
    void saveToUserNotFound() throws Exception {
        Utils.setUserContext(1);
    }

    @UserSavedSql
    @Test
    @DisplayName("DELETE - /item-type/{itemType}/{itemId}/type/{type} - Should delete an item from user")
    void deleteFromUser() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/user-saved/item-type/PLAN/1/type/SAVE"))
                .andExpectAll(status().isNoContent());
    }

    @UserSavedSql
    @Test
    @DisplayName("DELETE - /item-type/{itemType}/{itemId}/type/{type} - Should return 404 when item not found")
    void deleteFromUserNotFound() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/user-saved/item-type/PLAN/999/type/SAVE"))
                .andExpectAll(status().isNotFound());
    }

}
