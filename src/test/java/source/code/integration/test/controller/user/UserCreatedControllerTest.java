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
public class UserCreatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @UserSavedSql
    @Test
    @DisplayName("GET - /api/user-created/plans - Should return all created plans")
    void getUserPlans() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/plans"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(hasSize(1))
                );
    }

    @UserSavedSql
    @Test
    @DisplayName("GET - /api/user-created/recipes - Should return all created recipes")
    void getUserRecipes() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/recipes"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(hasSize(1))
                );
    }

    @UserSavedSql
    @Test
    @DisplayName("GET - /api/user-created/comments - Should return all created comments")
    void getUserComments() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/comments"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(hasSize(2))
                );
    }

    @UserSavedSql
    @Test
    @DisplayName("GET - /api/user-created/threads - Should return all created threads")
    void getUserThreads() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/user-created/threads"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(hasSize(1))
                );
    }
}
