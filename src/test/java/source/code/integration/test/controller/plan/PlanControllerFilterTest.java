package source.code.integration.test.controller.plan;

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
import source.code.repository.PlanRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class PlanControllerFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PlanRepository planRepository;

    private FilterDto buildFilterDto(String filterKey, Object value, FilterOperation operation) {
        FilterCriteria criteria = FilterCriteria.of(filterKey, value, operation);
        return FilterDto.of(new ArrayList<>(List.of(criteria)), FilterDataOption.AND);
    }

    private FilterDto buildFilterDto(String filterKey, Object value, FilterOperation operation, Boolean isPublic) {
        FilterCriteria criteria = FilterCriteria.of(filterKey, value, operation, isPublic);
        return FilterDto.of(new ArrayList<>(List.of(criteria)), FilterDataOption.AND);
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve private plans when isPublic = false by filtered by type")
    void filterPrivatePlansByType() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("TYPE", 1, FilterOperation.EQUAL, false);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered plans by plan type (Default isPublic = true), so should return 5/6 because 1 is private")
    void filterPlansByPlanType() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("TYPE", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(5))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered plans by plan category")
    void filterPlansByPlanCategory() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("CATEGORY", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(3)),
                        jsonPath("$.content[*].name", containsInAnyOrder(
                                "Beginner Strength", "Powerlifting", "Home Workout"
                        )),
                        jsonPath("$.content[*].categories[?(@.id == 1)].name",
                                everyItem(is("Strength Training")))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered plans by plan like")
    void filterPlansByPlanLike() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("LIKE", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(6))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered plans by plan save")
    void filterPlansByPlanSave() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("SAVE", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(6))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered plans by plan equipment")
    void filterPlansByPlanEquipment() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("EQUIPMENT", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter key")
    void filterPlansInvalidFilterKey() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("invalidKey", "value", FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter value")
    void filterPlansInvalidFilterValue() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("TYPE", "invalidValue", FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter operation")
    void filterPlansInvalidFilterOperation() throws Exception {
        Utils.setUserContext(1);

        String requestJson = """
                {
                    "filterCriteria": [{
                        "filterKey": "TYPE",
                        "value": 1,
                        "operation": "CONTAINS"
                    }],
                    "dataOption": "AND"
                }
                """;

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve plans saved by user 1 (user 1 saved plan 6)")
    void filterPlansSavedByUser() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("SAVED_BY_USER", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].name", is("Clean Eating"))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve plans liked by user 1 (user 1 liked plan 3)")
    void filterPlansLikedByUser() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("LIKED_BY_USER", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].name", is("Cardio Blast"))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve plans disliked by user 1 (user 1 disliked plan 9)")
    void filterPlansDislikedByUser() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("DISLIKED_BY_USER", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].name", is("5K Training"))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should retrieve public plans created by user 1")
    void filterPlansCreatedByUser1() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("CREATED_BY_USER", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[*].name", containsInAnyOrder(
                                "Beginner Strength", "Muscle Gain Diet"
                        ))
                );
    }

    @PlanSql
    @Test
    @DisplayName("POST - /filter - Should return empty when user has no plans")
    void filterPlansCreatedByUserNoResults() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("CREATED_BY_USER", 999, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/plans/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(0))
                );
    }
}
