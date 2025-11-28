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
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class UserSavedControllerWithTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId} - Should return all public saved items of a specific type SAVE for PLAN")
    void getAllFromUserSavePlan() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/type/SAVE/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(3)),
                        jsonPath("$.page.totalElements").value(3)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId} - Should return all public saved items of a specific type SAVE for RECIPE")
    void getAllFromUserSaveRecipe() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/RECIPE/type/SAVE/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(1)),
                        jsonPath("$.page.totalElements").value(1)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type} - Should return all public saved items of a specific type LIKE")
    void getAllFromUserLike() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/COMMENT/type/LIKE/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.page.totalElements").value(1)
                );

    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId} - Should return all items with DISLIKE type for COMMENT")
    void getAllFromUserDislikeComment() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/COMMENT/type/DISLIKE/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].id").value(2),
                        jsonPath("$.content[1].id").value(1),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId} - Should return all items with DISLIKE type for PLAN")
    void getAllFromUserDislikePlan() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/type/DISLIKE/user/2"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].id").value(3),
                        jsonPath("$.content[1].id").value(1),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId} - Should return all items with DISLIKE type for RECIPE")
    void getAllFromUserDislikeRecipe() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/RECIPE/type/DISLIKE/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].id").value(3),
                        jsonPath("$.content[1].id").value(1),
                        jsonPath("$.page.totalElements").value(2)
                );
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
        Utils.setUserContext(1);

        mockMvc.perform(post("/api/user-saved/item-type/PLAN/1/type/LIKE"))
                .andExpectAll(status().isCreated());
    }

    @UserSavedSql
    @Test
    @DisplayName("POST - /item-type/{itemType}/{itemId}/type/{type} - Should return 400 when trying to SAVE comment(forbidden interaction for this entity")
    void saveToUserSAVEComment() throws Exception {
        Utils.setUserContext(2);

        mockMvc.perform(post("/api/user-saved/item-type/COMMENT/1/type/SAVE"))
                .andExpectAll(status().isBadRequest());
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

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId}?sort=DESC - Should return items sorted DESC")
    void getAllFromUserSortDesc() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/type/SAVE/user/1")
                .param("sort", "createdAt,DESC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(3)),
                        jsonPath("$.content[0].id").value(3),
                        jsonPath("$.content[1].id").value(2),
                        jsonPath("$.content[2].id").value(1)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId}?sort=ASC - Should return items sorted ASC")
    void getAllFromUserSortAsc() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/PLAN/type/SAVE/user/1")
                .param("sort", "createdAt,ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(3)),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[1].id").value(2),
                        jsonPath("$.content[2].id").value(3)
                );
    }

    @WithMockUser
    @UserSavedSql
    @Test
    @DisplayName("GET - /item-type/{itemType}/type/{type}/user/{userId} - Should default to DESC when no sort param")
    void getAllFromUserDefaultSort() throws Exception {
        mockMvc.perform(get("/api/user-saved/item-type/COMMENT/type/LIKE/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").exists(),
                        jsonPath("$.page.totalElements").exists()
                );
    }
}
