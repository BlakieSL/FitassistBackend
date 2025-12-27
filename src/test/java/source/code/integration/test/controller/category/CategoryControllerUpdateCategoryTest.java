package source.code.integration.test.controller.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class CategoryControllerUpdateCategoryTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@CategorySql
	@Test
	@DisplayName("PATCH /FOOD/{id} - Should update a food category")
	void updateFoodCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Updated Food Category"));
		mockMvc.perform(patch("/api/categories/FOOD/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("PATCH /FOOD/{id} - Non-admin user should get 403 Forbidden")
	void updateFoodCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("User Food Update"));
		mockMvc.perform(patch("/api/categories/FOOD/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH /FOOD/{id} - Should return 404 for non-existent food category")
	void updateNonExistentFoodCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Non-existent Food Category"));
		mockMvc.perform(patch("/api/categories/FOOD/999").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound());
	}

	@CategorySql
	@Test
	@DisplayName("PATCH /ACTIVITY/{id} - Should update an activity category")
	void updateActivityCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Updated Activity Category"));
		mockMvc
			.perform(patch("/api/categories/ACTIVITY/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("PATCH /ACTIVITY/{id} - Non-admin user should get 403 Forbidden")
	void updateActivityCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("User Activity Update"));
		mockMvc
			.perform(patch("/api/categories/ACTIVITY/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH /ACTIVITY/{id} - Should return 404 for non-existent activity category")
	void updateNonExistentActivityCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Non-existent Activity Category"));
		mockMvc
			.perform(patch("/api/categories/ACTIVITY/999").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound());
	}

	@CategorySql
	@Test
	@DisplayName("PATCH /RECIPE/{id} - Should update a recipe category")
	void updateRecipeCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Updated Recipe Category"));
		mockMvc.perform(patch("/api/categories/RECIPE/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("PATCH /RECIPE/{id} - Non-admin user should get 403 Forbidden")
	void updateRecipeCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("User Recipe Update"));
		mockMvc.perform(patch("/api/categories/RECIPE/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH /RECIPE/{id} - Should return 404 for non-existent recipe category")
	void updateNonExistentRecipeCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Non-existent Recipe Category"));
		mockMvc
			.perform(patch("/api/categories/RECIPE/999").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound());
	}

	@CategorySql
	@Test
	@DisplayName("PATCH /PLAN/{id} - Should update a plan category")
	void updatePlanCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Updated Plan Category"));
		mockMvc.perform(patch("/api/categories/PLAN/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("PATCH /PLAN/{id} - Non-admin user should get 403 Forbidden")
	void updatePlanCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("User Plan Update"));
		mockMvc.perform(patch("/api/categories/PLAN/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("PATCH /PLAN/{id} - Should return 404 for non-existent plan category")
	void updateNonExistentPlanCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestBody = objectMapper.writeValueAsString(new CategoryCreateDto("Non-existent Plan Category"));
		mockMvc.perform(patch("/api/categories/PLAN/999").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound());
	}

}
