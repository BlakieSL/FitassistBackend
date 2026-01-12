package source.code.integration.test.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class UserSavedControllerWithoutTypeTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId} - Should return all saved items of a specific type")
	void getAllFromUser() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/ACTIVITY/user/1"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(2)),
					jsonPath("$.page.totalElements").value(2));
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId} - Should return all saved exercises with firstImageUrl")
	void getAllFromUserExercises() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/EXERCISE/user/1"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(2)),
					jsonPath("$.page.totalElements").value(2));
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId} - Should return all saved foods with firstImageUrl")
	void getAllFromUserFoods() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/FOOD/user/1"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(2)),
					jsonPath("$.page.totalElements").value(2));
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId} - Should return all saved threads with author image")
	void getAllFromUserThreads() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/FORUM_THREAD/user/1"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(1)),
					jsonPath("$.content[0].id").value(2),
					jsonPath("$.content[0].title").value("Protein intake question"),
					jsonPath("$.content[0].author.username").value("adminuser"),
					jsonPath("$.content[0].author.id").value(2), jsonPath("$.content[0].author.imageUrl").exists(),
					jsonPath("$.content[0].views").value(42), jsonPath("$.content[0].savesCount").value(2),
					jsonPath("$.content[0].commentsCount").value(2), jsonPath("$.page.totalElements").value(1));
	}

	@UserSavedSql
	@Test
	@DisplayName("POST - /item-type/{itemType}/{itemId} - Should save an item to user")
	void saveToUser() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(post("/api/user-saved/item-type/EXERCISE/2")).andExpectAll(status().isCreated());
	}

	@UserSavedSql
	@Test
	@DisplayName("POST - /item-type/{itemType}/{itemId} - Should return 409 when item already saved")
	void saveToUserAlreadySaved() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(post("/api/user-saved/item-type/EXERCISE/1")).andExpectAll(status().isConflict());
	}

	@UserSavedSql
	@Test
	@DisplayName("POST - /item-type/{itemType}/{itemId}- Should return 404 when item not found")
	void saveToUserNotFound() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(post("/api/user-saved/item-type/EXERCISE/999")).andExpectAll(status().isNotFound());
	}

	@UserSavedSql
	@Test
	@DisplayName("DELETE - /item-type/{itemType}/{itemId} - Should delete an item from user")
	void deleteFromUser() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/user-saved/item-type/EXERCISE/1")).andExpectAll(status().isNoContent());
	}

	@UserSavedSql
	@Test
	@DisplayName("DELETE - /item-type/{itemType}/{itemId} - Should return 404 when item not found")
	void deleteFromUserNotFound() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/user-saved/item-type/EXERCISE/999")).andExpectAll(status().isNotFound());
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId}?sort=DESC - Should return items sorted DESC")
	void getAllFromUserWithoutTypeSortDesc() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/ACTIVITY/user/1").param("sort", "createdAt,DESC"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(2)),
					jsonPath("$.content[0].id").exists());
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId}?sort=ASC - Should return items sorted ASC")
	void getAllFromUserWithoutTypeSortAsc() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/EXERCISE/user/1").param("sort", "createdAt,ASC"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(2)),
					jsonPath("$.content[0].id").exists());
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId} - Should default to DESC when no sort param")
	void getAllFromUserWithoutTypeDefaultSort() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/FOOD/user/1"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(2)),
					jsonPath("$.content[0].id").exists(), jsonPath("$.page.totalElements").value(2));
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId}?sort=DESC - Should return threads sorted DESC")
	void getAllFromUserThreadsSortDesc() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/FORUM_THREAD/user/1").param("sort", "createdAt,DESC"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(1)),
					jsonPath("$.content[0].id").value(2));
	}

	@WithMockUser
	@UserSavedSql
	@Test
	@DisplayName("GET - /item-type/{itemType}/user/{userId}?sort=ASC - Should return threads sorted ASC")
	void getAllFromUserThreadsSortAsc() throws Exception {
		mockMvc.perform(get("/api/user-saved/item-type/FORUM_THREAD/user/1").param("sort", "createdAt,ASC"))
			.andExpectAll(status().isOk(), jsonPath("$.content").value(hasSize(1)),
					jsonPath("$.content[0].id").value(2));
	}

}
