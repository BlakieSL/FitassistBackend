package source.code.integration.test.controller.food;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
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

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class FoodControllerGetFilteredTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return filtered foods by Category")
    void getFilteredFoodsByCategory() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CATEGORY", 1, FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].name").value("Apple"),
                        jsonPath("$[1].name").value("Banana"),
                        jsonPath("$.length()").value(2)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return filtered foods by Calories")
    void getFilteredFoodsByCalories() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CALORIES", 100, FilterOperation.LESS_THAN
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(2)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return filtered foods by Protein")
    void getFilteredFoodsByProtein() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "PROTEIN", new BigDecimal("5.0"), FilterOperation.GREATER_THAN
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(3)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return filtered foods by Fat")
    void getFilteredFoodsByFat() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "FAT", new BigDecimal("3.0"), FilterOperation.GREATER_THAN
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].name").value("Chicken Breast"),
                        jsonPath("$[1].name").value("Eggs"),
                        jsonPath("$.length()").value(2)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return filtered foods by Carbohydrates")
    void getFilteredFoodsByCarbohydrates() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CARBOHYDRATES", new BigDecimal("20.0"), FilterOperation.LESS_THAN
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].name").value("Chicken Breast"),
                        jsonPath("$[1].name").value("Eggs"),
                        jsonPath("$[2].name").value("Greek Yogurt"),
                        jsonPath("$.length()").value(3)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return filtered foods by Save")
    void getFilteredFoodsBySave() throws Exception {
        Utils.setUserContext(1);

        FilterCriteria criteria = FilterCriteria.of(
                "SAVE", 1, FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1)
                );
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return 400 when invalid filter key")
    void getFilteredFoodsByInvalidFilterKey() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "INVALID_KEY", 1, FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(status().isBadRequest());
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return 400 when invalid filter value")
    void getFilteredFoodsByInvalidFilterValue() throws Exception {
        FilterCriteria criteria = FilterCriteria.of(
                "CATEGORY", "invalid_value", FilterOperation.EQUAL
        );
        FilterDto filterDto = FilterDto.of(List.of(criteria), FilterDataOption.AND);

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpectAll(status().isBadRequest());
    }

    @WithMockUser
    @FoodSql
    @Test
    @DisplayName("POST - /filter - Should return 400 when operation is invalid")
    void getFilteredFoodsByInvalidOperation() throws Exception {
        String requestJson = """
                {
                    "filterCriteria": [{
                        "filterKey": "CALORIES",
                        "value": 100,
                        "operation": "DOES_NOT_BEGIN_WITH"
                    }],
                    "dataOption": "AND"
                }
                """;

        mockMvc.perform(post("/api/foods/filter")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpectAll(status().isBadRequest());
    }
}
