package source.code.integration.test.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class UserCreatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/plans/user/{userId} - Should return all created plans for another user")
    void getUserPlansAnother() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(get("/api/user-created/plans/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/plans/user/{userId} - Should return all created plans for own")
    void getUserPlansOwn() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/plans/user/{userId}", 1))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/recipes/user/{userId} - Should return all created recipes for another")
    void getUserRecipesOwn() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(get("/api/user-created/recipes/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/recipes/user/{userId} - Should return all created recipes for own")
    void getUserRecipesAnother() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/recipes/user/{userId}", 1))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/comments - Should return all created comments for user")
    void getUserComments() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/comments/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2)
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/threads - Should return all created threads for user")
    void getUserThreads() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/threads/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(1)),
                        jsonPath("$.page.totalElements").value(1)
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/plans/user/{userId}?sort=DESC - Should return plans sorted by createdAt DESC")
    void getUserPlansSortedDesc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/plans/user/1")
                .param("sort", "DESC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[1].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/plans/user/{userId}?sort=ASC - Should return plans sorted by createdAt ASC")
    void getUserPlansSortedAsc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/plans/user/1")
                .param("sort", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[1].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/recipes/user/{userId}?sort=DESC - Should return recipes sorted by createdAt DESC")
    void getUserRecipesSortedDesc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/recipes/user/1")
                .param("sort", "DESC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[1].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/recipes/user/{userId}?sort=ASC - Should return recipes sorted by createdAt ASC")
    void getUserRecipesSortedAsc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/recipes/user/1")
                .param("sort", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[1].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/comments/user/{userId}?sort=DESC - Should return comments sorted by createdAt DESC")
    void getUserCommentsSortedDesc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/comments/user/1")
                .param("sort", "DESC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[1].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/comments/user/{userId}?sort=ASC - Should return comments sorted by createdAt ASC")
    void getUserCommentsSortedAsc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/comments/user/1")
                .param("sort", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[1].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/threads/user/{userId}?sort=DESC - Should return threads sorted by createdAt DESC")
    void getUserThreadsSortedDesc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/threads/user/1")
                .param("sort", "DESC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(1)),
                        jsonPath("$.page.totalElements").value(1),
                        jsonPath("$.content[0].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/threads/user/{userId}?sort=ASC - Should return threads sorted by createdAt ASC")
    void getUserThreadsSortedAsc() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/threads/user/1")
                .param("sort", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(1)),
                        jsonPath("$.page.totalElements").value(1),
                        jsonPath("$.content[0].id").exists()
                );
    }

    @UserCreatedSql
    @Test
    @DisplayName("GET - /api/user-created/plans/user/{userId} - Should default to DESC when no sort param")
    void getUserPlansDefaultSort() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/plans/user/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(2)),
                        jsonPath("$.page.totalElements").value(2)
                );
    }
}
