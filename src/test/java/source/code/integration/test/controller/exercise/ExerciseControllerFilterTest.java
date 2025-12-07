package source.code.integration.test.controller.exercise;

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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class ExerciseControllerFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FilterDto buildFilterDto(String filterKey, Object value, FilterOperation operation) {
        FilterCriteria criteria = FilterCriteria.of(filterKey, value, operation);
        return FilterDto.of(List.of(criteria), FilterDataOption.AND);
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises by expertise level")
    void getFilteredExercises() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("EXPERTISE_LEVEL", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].expertiseLevel.id").value(1)
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises by equipment")
    void getFilteredExercisesByEquipment() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("EQUIPMENT", 4, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2))
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises by mechanics type")
    void getFilteredExercisesByMechanicsType() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("MECHANICS_TYPE", 1, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(3))
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises by force type")
    void getFilteredExercisesByForceType() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("FORCE_TYPE", 3, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2))
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises by target muscle")
    void getFilteredExercisesByTargetMuscle() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("TARGET_MUSCLE", 2, FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1))
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises excluding target muscle with NOT_EQUAL")
    void getFilteredExercisesByTargetMuscleNotEqual() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("TARGET_MUSCLE", 2, FilterOperation.NOT_EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(4))
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should retrieve filtered exercises by saves")
    void getFilteredExercisesBySaves() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("SAVE", 0, FilterOperation.GREATER_THAN);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(2))
                );
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter key")
    void getFilteredExercisesWithInvalidKey() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("invalidKey", "value", FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invalid filter value")
    void getFilteredExercisesWithInvalidValue() throws Exception {
        Utils.setUserContext(1);
        FilterDto filterDto = buildFilterDto("EXPERTISE_LEVEL", "invalidValue", FilterOperation.EQUAL);
        String json = objectMapper.writeValueAsString(filterDto);

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @ExerciseSql
    @Test
    @DisplayName("POST - /filter - Should return 400, when invlaid filter operation")
    void getFilteredExercisesWithInvalidOperation() throws Exception {
        Utils.setUserContext(1);

        String requestJson = """
                {
                    "filterCriteria": [{
                        "filterKey": "EXPERTISE_LEVEL",
                        "value": 1,
                        "operation": "CONTAINS"
                    }],
                    "dataOption": "AND"
                }
                """;

        mockMvc.perform(post("/api/exercises/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

}
