package source.code.integration.test.activity;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;

import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.filter.FilterDto;
import source.code.helper.Enum.filter.FilterDataOption;
import source.code.helper.Enum.filter.FilterOperation;
import source.code.integration.config.TestConfig;
import source.code.integration.containers.MySqlRedisContainers;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        "classpath:activity/schema/drop-schema.sql",
        "classpath:activity/schema/create-schema.sql",
})
@SqlMergeMode(value = SqlMergeMode.MergeMode.MERGE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Import(TestConfig.class)
public class ActivityTest extends MySqlRedisContainers {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ActivitySql
    @Test
    @DisplayName("POST - / - Should create a new activity when user is admin")
    void createActivityAdmin() throws Exception {
        Utils.setAdminContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Swimming");
        dto.setMet(BigDecimal.valueOf(7.5));
        dto.setCategoryId(1);

        mockMvc.perform(post("/api/activities")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Swimming"),
                        jsonPath("$.met").value(7.5),
                        jsonPath("$.categoryId").value(1)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - / - Should return 403 when user is not admin")
    void createActivityNotAdmin() throws Exception {
        Utils.setUserContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Swimming");
        dto.setMet(BigDecimal.valueOf(7.5));
        dto.setCategoryId(1);

        mockMvc.perform(post("/api/activities")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isForbidden());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing activity when user is admin")
    void updateActivityAdmin() throws Exception {
        Utils.setAdminContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Cycling");
        dto.setMet(BigDecimal.valueOf(8.0));
        dto.setCategoryId(2);

        mockMvc.perform(patch("/api/activities/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isNoContent());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when user is not admin")
    void updateActivityNotAdmin() throws Exception {
        Utils.setUserContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Cycling");
        dto.setMet(BigDecimal.valueOf(8.0));
        dto.setCategoryId(2);

        mockMvc.perform(patch("/api/activities/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isForbidden());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when activity does not exist")
    void updateActivityNotFound() throws Exception {
        Utils.setAdminContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Cycling");
        dto.setMet(BigDecimal.valueOf(8.0));
        dto.setCategoryId(2);

        mockMvc.perform(patch("/api/activities/999")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isNotFound());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /{id} - Should return 400 when patch is invalid")
    void updateActivityInvalidPatch() throws Exception {
        Utils.setAdminContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Cycling");
        dto.setMet(BigDecimal.valueOf(-1));
        dto.setCategoryId(2);

        mockMvc.perform(patch("/api/activities/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isBadRequest());
    }

    @ActivitySql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing activity when user is admin")
    void deleteActivityAdmin() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/activities/1"))
                .andExpectAll(status().isNoContent());
    }

    @ActivitySql
    @Test
    @DisplayName("DELETE - /{id} - Should return 403 when user is not admin")
    void deleteActivityNotAdmin() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/activities/1"))
                .andExpectAll(status().isForbidden());
    }

    @ActivitySql
    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when activity does not exist")
    void deleteActivityNotFound() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/activities/999"))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("GET - /{id} - Should return an existing activity")
    void getActivity() throws Exception {
        mockMvc.perform(get("/api/activities/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Brisk Walking"),
                        jsonPath("$.met").value(3.5),
                        jsonPath("$.categoryId").value(1),
                        jsonPath("$.categoryName").value("Walking")
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("GET - /{id} - Should return 404 when activity does not exist")
    void getActivityNotFound() throws Exception {
        mockMvc.perform(get("/api/activities/999"))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("GET - / - Should return all activities")
    void getAllActivities() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Brisk Walking"),
                        jsonPath("$[0].met").value(3.5),
                        jsonPath("$[0].categoryId").value(1)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - / - Should return empty list when no activities exist")
    void getAllActivitiesEmpty() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(0)
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /filter - Should return filtered activities by Category")
    void getFilteredActivitiesByCategory() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CATEGORY", 2, FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/activities/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Jogging"),
                        jsonPath("$[0].met").value(6.0),
                        jsonPath("$[0].categoryId").value(2)
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /filter - Should return filtered activities by MET")
    void getFilteredActivitiesByMet() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "MET", BigDecimal.valueOf(7.5), FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/activities/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Lap Swimming"),
                        jsonPath("$[0].met").value(7.5),
                        jsonPath("$[0].categoryId").value(4)
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter key")
    void getFilteredActivitiesInvalidFilterKey() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "invalidKey", 2, FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/activities/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter value")
    void getFilteredActivitiesInvalidFilterValue() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CATEGORY", "invalidValue", FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/activities/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /filter - Should return 400, when operation is invalid")
    void getFilteredActivitiesInvalidOperation() throws Exception {
        String requestJson = """
                {
                    "filterCriteria": [{
                        "filterKey": "CATEGORY",
                        "value": 2,
                        "operation": "CONTAINS"
                    }],
                    "dataOption": "AND"
                }
                """;

        mockMvc.perform(post("/api/activities/filter")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpectAll(
                        status().isBadRequest()
                );
    }
}
