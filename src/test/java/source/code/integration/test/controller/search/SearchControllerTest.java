package source.code.integration.test.controller.search;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import source.code.integration.config.MockSearchConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.service.declaration.search.LuceneInitialLoadService;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetupWithLucene
@Import({MockSearchConfig.class, MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LuceneInitialLoadService luceneInitialLoadService;

    @AfterEach
    void cleanUp() {
        luceneInitialLoadService.clearIndexDirectory();
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query= - Should search for all 5 entities")
    void searchAllEntities() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search").param("query", "text"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(5))
                );
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query= - Should return empty list for non-existent query")
    void searchNonExistentQuery() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search").param("query", "nonexistent"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query= - Should perform fuzzy search for 1 incorrect character")
    void searchWithFuzzyQuery() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search").param("query", "testi"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(5))
                );
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query= - Should perform fuzzy search for 2 incorrect characters")
    void searchWithFuzzyQueryTwoChars() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search").param("query", "tesit"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(5))
                );
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query=&type= - Should search for food entities")
    void searchFoodEntities() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search")
                        .param("query", "Greek")
                        .param("type", "Food"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1))
                );
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query=&type= - Should return empty list for activity name when filtering by Food")
    void searchNonExistentFoodQuery() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search")
                        .param("query", "Running")
                        .param("type", "Food"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );
    }

    @WithMockUser
    @SearchSql
    @Test
    @DisplayName("GET - ?query=&type= - Should return 400 for invalid type")
    void searchWithInvalidType() throws Exception {
        luceneInitialLoadService.indexAll();

        mockMvc.perform(get("/api/search")
                        .param("query", "text")
                        .param("type", "InvalidType"))
                .andExpect(status().isBadRequest());
    }
}
