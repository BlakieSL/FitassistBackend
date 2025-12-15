package source.code.integration.test.controller.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.helper.Enum.filter.FilterDataOption;
import source.code.helper.Enum.filter.FilterOperation;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class ActivityControllerTest {

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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Swimming"),
                        jsonPath("$.met").value(7.5),
                        jsonPath("$.category.id").value(1)
                );
    }

    @Test
    @DisplayName("POST - / - Should return 403 when user is not admin")
    void createActivityNotAdmin() throws Exception {
        Utils.setUserContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Swimming");
        dto.setMet(BigDecimal.valueOf(7.5));
        dto.setCategoryId(1);

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when user is not admin")
    void updateActivityNotAdmin() throws Exception {
        Utils.setUserContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Cycling");
        dto.setMet(BigDecimal.valueOf(8.0));
        dto.setCategoryId(2);

        mockMvc.perform(patch("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpectAll(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when activity does not exist")
    void updateActivityNotFound() throws Exception {
        Utils.setAdminContext(1);

        ActivityCreateDto dto = new ActivityCreateDto();
        dto.setName("Cycling");
        dto.setMet(BigDecimal.valueOf(8.0));
        dto.setCategoryId(2);

        mockMvc.perform(patch("/api/activities/999")
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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

    @ActivitySql
    @Test
    @DisplayName("GET - /{id} - Should return activity with saved=true when user has saved it")
    void getActivity_ShouldReturnActivityWithSavedTrue_WhenUserHasSavedIt() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/activities/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Brisk Walking"),
                        jsonPath("$.met").value(3.5),
                        jsonPath("$.category.id").value(1),
                        jsonPath("$.category.name").value("Walking"),
                        jsonPath("$.images..imageUrls").isArray(),
                        jsonPath("$.savesCount").value(1),
                        jsonPath("$.saved").value(true)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("GET - /{id} - Should return activity with saved=false when user has not saved it")
    void getActivity_ShouldReturnActivityWithSavedFalse_WhenUserHasNotSavedIt() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(get("/api/activities/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Brisk Walking"),
                        jsonPath("$.savesCount").value(1),
                        jsonPath("$.saved").value(false)
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
    @DisplayName("POST - /filter - Should return filtered activities by Category")
    void getFilteredActivitiesByCategory() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CATEGORY", 2, FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/activities/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").exists(),
                        jsonPath("$.content[0].name").value("Jogging"),
                        jsonPath("$.content[0].met").value(6.0),
                        jsonPath("$.content[0].category.id").value(2)
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /filter - Should return filtered activities by MET")
    void getFilteredActivitiesByMet() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "MET", BigDecimal.valueOf(7.5), FilterOperation.GREATER_THAN_EQUAL);
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/activities/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.length()").value(2)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - /{id}/calculate-calories - Should calculate calories burned for an activity heath-related information already saved")
    void calculateActivityCaloriesBurned() throws Exception {
        Utils.setUserContext(1);
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto();
        request.setTime(60);

        mockMvc.perform(post("/api/activities/1/calculate-calories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.caloriesBurned").hasJsonPath()
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /{id}/calculate-calories - Should calculate calories burned for an activity with health-related information provided in request")
    void calculateActivityCaloriesBurnedWithHealthInfo() throws Exception {
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto();
        request.setTime(60);
        request.setWeight(new BigDecimal("80.0"));

        mockMvc.perform(post("/api/activities/1/calculate-calories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.caloriesBurned").hasJsonPath()
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - /{id}/calculate-calories - Should return 400 when health-related information is not provided nor already saved")
    void calculateActivityCaloriesBurnedWithoutHealthInfo() throws Exception {
        Utils.setUserContext(2);
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto();
        request.setTime(60);

        mockMvc.perform(post("/api/activities/1/calculate-calories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @WithMockUser
    @ActivitySql
    @Test
    @DisplayName("POST - /{id}/calculate-calories - Should return 404 when activity does not exist")
    void calculateActivityCaloriesBurnedNotFound() throws Exception {
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto();
        request.setTime(60);
        request.setWeight(new BigDecimal("80.0"));

        mockMvc.perform(post("/api/activities/999/calculate-calories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}
