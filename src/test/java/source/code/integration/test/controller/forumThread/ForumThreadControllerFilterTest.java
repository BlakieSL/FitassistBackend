package source.code.integration.test.controller.forumThread;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.helper.Enum.filter.FilterDataOption;
import source.code.helper.Enum.filter.FilterOperation;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class ForumThreadControllerFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FilterDto buildFilterDto(String filterKey, Object value, FilterOperation operation) {
        FilterCriteria criteria = FilterCriteria.of(filterKey, value, operation);
        return FilterDto.of(List.of(criteria), FilterDataOption.AND);
    }

    @ForumThreadSql
    @Test
    @DisplayName("POST - /filter - Should filter threads created by user 1 (user 1 created thread 1 'Favorite Apps')")
    void filterThreadsCreatedByUser1() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("CREATED_BY_USER", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/threads/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].title", is("Favorite Apps"))
                );
    }


    @ForumThreadSql
    @Test
    @DisplayName("POST - /filter - Should return empty when user has no threads")
    void filterThreadsCreatedByUserNoResults() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("CREATED_BY_USER", 999, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/threads/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(0))
                );
    }

    @ForumThreadSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter key")
    void filterThreadsInvalidKey() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("INVALID_KEY", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/threads/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @ForumThreadSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter value")
    void filterThreadsInvalidValue() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("CREATED_BY_USER", "invalid", FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/threads/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @ForumThreadSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter operation")
    void filterThreadsInvalidOperation() throws Exception {
        Utils.setUserContext(1);

        String requestJson = """
                {
                    "filterCriteria": [{
                        "filterKey": "CREATED_BY_USER",
                        "value": 1,
                        "operation": "CONTAINS"
                    }],
                    "dataOption": "AND"
                }
                """;

        mockMvc.perform(post("/api/threads/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
