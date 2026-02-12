package com.fitassist.backend.integration.test.controller.category;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class CategoryControllerDeleteCategoryTest {

	@Autowired
	private MockMvc mockMvc;

	@CategorySql
	@Test
	@DisplayName("DELETE /FOOD/{id} - Should delete a food category when not associated with any food items")
	void deleteFoodCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/FOOD/6")).andExpect(status().isNoContent());
		mockMvc.perform(get("/api/categories/FOOD/6")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /FOOD/{id} - Should return 404 for non-existent food category")
	void deleteNonExistentFoodCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/FOOD/999")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /FOOD/{id} - Should return 403 for non-admin user")
	void deleteFoodCategoryAsNonAdmin() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/categories/FOOD/1")).andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE /FOOD/{id} - Should return 500 for food category with associated food items")
	void deleteFoodCategoryWithAssociations() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/FOOD/1")).andExpect(status().isInternalServerError());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE /ACTIVITY/{id} - Should delete an activity category")
	void deleteActivityCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/ACTIVITY/6")).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /ACTIVITY/{id} - Should return 404 for non-existent activity category")
	void deleteNonExistentActivityCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/ACTIVITY/999")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /ACTIVITY/{id} - Should return 403 for non-admin user")
	void deleteActivityCategoryAsNonAdmin() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/categories/ACTIVITY/1")).andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE /ACTIVITY/{id} - Should return 500 for activity category with associated activities")
	void deleteActivityCategoryWithAssociations() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/ACTIVITY/1")).andExpect(status().isInternalServerError());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE /RECIPE/{id} - Should delete a recipe category")
	void deleteRecipeCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/RECIPE/6")).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/categories/RECIPE/6")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /RECIPE/{id} - Should return 404 for non-existent recipe category")
	void deleteNonExistentRecipeCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/RECIPE/999")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /RECIPE/{id} - Should return 403 for non-admin user")
	void deleteRecipeCategoryAsNonAdmin() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/categories/RECIPE/1")).andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE /RECIPE/{id} - Should return 409 for recipe category with associated recipes")
	void deleteRecipeCategoryWithAssociations() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/RECIPE/1")).andExpect(status().isInternalServerError());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE /PLAN/{id} - Should delete a plan category")
	void deletePlanCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/PLAN/6")).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/categories/PLAN/6")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /PLAN/{id} - Should return 404 for non-existent plan category")
	void deleteNonExistentPlanCategory() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/PLAN/999")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /PLAN/{id} - Should return 403 for non-admin user")
	void deletePlanCategoryAsNonAdmin() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/categories/PLAN/1")).andExpect(status().isForbidden());
	}

	@CategorySql
	@Test
	@DisplayName("DELETE/PLAN/{id} - Should return 500 for plan category with associated plans")
	void deletePlanCategoryWithAssociations() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/categories/PLAN/1")).andExpect(status().isInternalServerError());
	}

}
