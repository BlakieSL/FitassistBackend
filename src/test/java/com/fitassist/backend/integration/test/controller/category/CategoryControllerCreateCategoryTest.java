package com.fitassist.backend.integration.test.controller.category;

import tools.jackson.databind.ObjectMapper;
import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.integration.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class CategoryControllerCreateCategoryTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@CategorySql
	@Test
	@DisplayName("POST /FOOD - Should create a food category")
	void createFoodCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Food Category"));
		mockMvc.perform(post("/api/categories/FOOD").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpectAll(status().isCreated(), jsonPath("$.name").value("New Food Category"));
	}

	@CategorySql
	@Test
	@DisplayName("POST /FOOD - Non-admin user should get 403 Forbidden")
	void createFoodCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Food Category"));
		mockMvc.perform(post("/api/categories/FOOD").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("POST /ACTIVITY - Should create an activity category")
	void createActivityCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Activity Category"));
		mockMvc.perform(post("/api/categories/ACTIVITY").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpectAll(status().isCreated(), jsonPath("$.name").value("New Activity Category"));
	}

	@CategorySql
	@Test
	@DisplayName("POST /ACTIVITY - Non-admin user should get 403 Forbidden")
	void createActivityCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Activity Category"));
		mockMvc.perform(post("/api/categories/ACTIVITY").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("POST /RECIPE - Should create a recipe category")
	void createRecipeCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Recipe Category"));
		mockMvc.perform(post("/api/categories/RECIPE").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpectAll(status().isCreated(), jsonPath("$.name").value("New Recipe Category"));
	}

	@CategorySql
	@Test
	@DisplayName("POST /RECIPE - Non-admin user should get 403 Forbidden")
	void createRecipeCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Recipe Category"));
		mockMvc.perform(post("/api/categories/RECIPE").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("POST /PLAN - Should create a plan category")
	void createPlanCategory() throws Exception {
		Utils.setAdminContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Plan Category"));
		mockMvc.perform(post("/api/categories/PLAN").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpectAll(status().isCreated(), jsonPath("$.name").value("New Plan Category"));
	}

	@CategorySql
	@Test
	@DisplayName("POST /PLAN - Non-admin user should get 403 Forbidden")
	void createPlanCategoryAsUserShouldForbid() throws Exception {
		Utils.setUserContext(1);
		String requestDto = objectMapper.writeValueAsString(new CategoryCreateDto("New Plan Category"));
		mockMvc.perform(post("/api/categories/PLAN").contentType(MediaType.APPLICATION_JSON).content(requestDto))
			.andExpect(status().isForbidden());
	}

}
