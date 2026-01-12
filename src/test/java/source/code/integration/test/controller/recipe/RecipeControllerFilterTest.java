package source.code.integration.test.controller.recipe;

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
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class RecipeControllerFilterTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private FilterDto buildFilterDto(String filterKey, Object value, FilterOperation operation) {
		FilterCriteria criteria = FilterCriteria.of(filterKey, value, operation);
		return FilterDto.of(List.of(criteria), FilterDataOption.AND);
	}

	private FilterDto buildFilterDto(String filterKey, Object value, FilterOperation operation, Boolean isPublic) {
		FilterCriteria criteria = FilterCriteria.of(filterKey, value, operation, isPublic);
		return FilterDto.of(List.of(criteria), FilterDataOption.AND);
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter public recipes and user's private recipes when isPublic = false by category")
	void filterPrivateRecipesByCategory() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("CATEGORY", 1, FilterOperation.EQUAL, false);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(3)));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter recipes by category (Default isPublic = true)")
	void filterRecipesByCategory() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("CATEGORY", 1, FilterOperation.EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(2)));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter recipes excluding category with NOT_EQUAL")
	void filterRecipesByCategoryNotEqual() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("CATEGORY", 1, FilterOperation.NOT_EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(2)));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter recipes by food")
	void filterRecipesByFood() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("FOODS", 2, FilterOperation.EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(2)),
					jsonPath("$.content[*].name", containsInAnyOrder("Grilled Chicken", "Chicken Rice Bowl")),
					jsonPath("$.content[*].foods[?(@.id == 2)].name", everyItem(is("Chicken Breast"))));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter recipes excluding food with NOT_EQUAL")
	void filterRecipesByFoodNotEqual() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("FOODS", 2, FilterOperation.NOT_EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(2)));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter recipes by save count greater than 0")
	void filterRecipesBySave() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("SAVE", 0, FilterOperation.GREATER_THAN);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(2)));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should filter recipes by like count less than or equal to 1")
	void filterRecipesByLike() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("LIKE", 1, FilterOperation.LESS_THAN_EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(4)));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should return 400, when invalid filter key")
	void filterRecipesInvalidKey() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("INVALID_KEY", "value", FilterOperation.EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isBadRequest());
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should return 400, when invalid filter value")
	void filterRecipesInvalidValue() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("CATEGORY", "invalid", FilterOperation.EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isBadRequest());
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should return 400, when invalid filter operation")
	void filterRecipesInvalidOperation() throws Exception {
		Utils.setUserContext(1);

		String requestJson = """
				{
				    "filterCriteria": [{
				        "filterKey": "CATEGORY",
				        "value": 1,
				        "operation": "CONTAINS"
				    }],
				    "dataOption": "AND"
				}
				""";

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpectAll(status().isBadRequest());
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should retrieve public recipes created by user 1")
	void filterRecipesCreatedByUser1() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("CREATED_BY_USER", 1, FilterOperation.EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(2)),
					jsonPath("$.content[*].name", containsInAnyOrder("Vegetable Stir Fry", "Grilled Chicken")));
	}

	@RecipeSql
	@Test
	@DisplayName("POST - /filter - Should return empty when user has no recipes")
	void filterRecipesCreatedByUserNoResults() throws Exception {
		Utils.setUserContext(1);
		FilterDto filterDto = buildFilterDto("CREATED_BY_USER", 999, FilterOperation.EQUAL);
		String json = objectMapper.writeValueAsString(filterDto);

		mockMvc.perform(post("/api/recipes/filter").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpectAll(status().isOk(), jsonPath("$.content", hasSize(0)));
	}

}
