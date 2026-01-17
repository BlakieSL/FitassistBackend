package com.fitassist.backend.integration.test.controller.category;

import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class CategoryControllerGetCategoryTest {

	@Autowired
	private MockMvc mockMvc;

	@CategorySql
	@WithMockUser
	@Test
	@DisplayName("GET - /FOOD/{id} - Should return food category by ID")
	void getFoodCategoryById() throws Exception {
		mockMvc.perform(get("/api/categories/FOOD/1"))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Fruits"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /FOOD/{id} - Should return 404 for non-existing food category")
	void getFoodCategoryByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/categories/FOOD/999")).andExpectAll(status().isNotFound());
	}

	@CategorySql
	@WithMockUser
	@Test
	@DisplayName("GET - /ACTIVITY/{id} - Should return activity category by ID")
	void getActivityCategoryById() throws Exception {
		mockMvc.perform(get("/api/categories/ACTIVITY/1"))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Cardio"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /ACTIVITY/{id} - Should return 404 for non-existing activity category")
	void getActivityCategoryByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/categories/ACTIVITY/999")).andExpectAll(status().isNotFound());
	}

	@CategorySql
	@WithMockUser
	@Test
	@DisplayName("GET - /RECIPE/{id} - Should return recipe category by ID")
	void getRecipeCategoryById() throws Exception {
		mockMvc.perform(get("/api/categories/RECIPE/1"))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Breakfast"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /RECIPE/{id} - Should return 404 for non-existing recipe category")
	void getRecipeCategoryByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/categories/RECIPE/999")).andExpectAll(status().isNotFound());
	}

	@CategorySql
	@WithMockUser
	@Test
	@DisplayName("GET - /PLAN/{id} - Should return plan category by ID")
	void getPlanCategoryById() throws Exception {
		mockMvc.perform(get("/api/categories/PLAN/1"))
			.andExpectAll(status().isOk(), jsonPath("$.name").value("Beginner"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /PLAN/{id} - Should return 404 for non-existing plan category")
	void getPlanCategoryByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/categories/PLAN/999")).andExpectAll(status().isNotFound());
	}

}
