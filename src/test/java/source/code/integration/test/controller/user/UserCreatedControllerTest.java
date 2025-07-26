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
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class})
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
                        jsonPath("$").value(hasSize(1))
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
                        jsonPath("$").value(hasSize(2))
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
                        jsonPath("$").value(hasSize(1))
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
                        jsonPath("$").value(hasSize(2))
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
                        jsonPath("$").value(hasSize(2))
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
                        jsonPath("$").value(hasSize(1))
                );
    }
}
