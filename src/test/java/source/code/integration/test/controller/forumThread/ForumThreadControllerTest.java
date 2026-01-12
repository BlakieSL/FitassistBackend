package source.code.integration.test.controller.forumThread;

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
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class ForumThreadControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@ForumThreadSql
	@Test
	@DisplayName("POST - / - Should create a forum thread")
	public void createForumThread_ShouldCreateForumThread() throws Exception {
		Utils.setUserContext(1);

		ForumThreadCreateDto createDto = new ForumThreadCreateDto();
		createDto.setTitle("New Thread Title");
		createDto.setText("This is the content of the new thread");
		createDto.setThreadCategoryId(1);

		mockMvc
			.perform(post("/api/threads").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpectAll(status().isOk(), jsonPath("$.id").exists(), jsonPath("$.title").value("New Thread Title"),
					jsonPath("$.text").value("This is the content of the new thread"),
					jsonPath("$.category.id").value(1), jsonPath("$.category.name").exists(),
					jsonPath("$.author.id").value(1), jsonPath("$.saved").exists());
	}

	@ForumThreadSql
	@Test
	@DisplayName("PATCH - /{forumThreadId} - Should update a forum thread, when user is owner")
	void updateForumThread_ShouldUpdateForumThread_WhenUserIsOwner() throws Exception {
		Utils.setUserContext(1);

		String patchJson = """
				{
				    "title": "Updated Title",
				    "text": "Updated content"
				}
				""";

		mockMvc.perform(patch("/api/threads/1").contentType(MediaType.APPLICATION_JSON).content(patchJson))
			.andExpect(status().isOk());
	}

	@ForumThreadSql
	@Test
	@DisplayName("PATCH - /{forumThreadId} - Should update a forum thread, when user is admin")
	void updateForumThread_ShouldUpdateForumThread_WhenUserIsAdmin() throws Exception {
		Utils.setAdminContext(2);

		String patchJson = """
				{
				    "title": "Admin Updated Title",
				    "text": "Admin updated content"
				}
				""";

		mockMvc.perform(patch("/api/threads/1").contentType(MediaType.APPLICATION_JSON).content(patchJson))
			.andExpect(status().isOk());
	}

	@ForumThreadSql
	@Test
	@DisplayName("PATCH - /{forumThreadId} - Should return 403 when user is not owner or admin")
	void updateForumThread_ShouldReturn403_WhenUserIsNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);

		String patchJson = """
				{
				    "title": "Unauthorized Update",
				    "text": "Should not work"
				}
				""";

		mockMvc.perform(patch("/api/threads/1").contentType(MediaType.APPLICATION_JSON).content(patchJson))
			.andExpect(status().isForbidden());
	}

	@ForumThreadSql
	@Test
	@DisplayName("DELETE - /{forumThreadId} - Should delete a forum thread, when user is owner")
	void deleteForumThread_ShouldDeleteForumThread_WhenUserIsOwner() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/threads/1")).andExpect(status().isOk());
	}

	@ForumThreadSql
	@Test
	@DisplayName("DELETE - /{forumThreadId} - Should delete a forum thread, when user is admin")
	void deleteForumThread_ShouldDeleteForumThread_WhenUserIsAdmin() throws Exception {
		Utils.setAdminContext(2);

		mockMvc.perform(delete("/api/threads/1")).andExpect(status().isOk());
	}

	@ForumThreadSql
	@Test
	@DisplayName("DELETE - /{forumThreadId} - Should return 403 when user is not owner or admin")
	void deleteForumThread_ShouldReturn403_WhenUserIsNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);

		mockMvc.perform(delete("/api/threads/1")).andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("DELETE - /{forumThreadId} - Should return 404 when forum thread does not exist")
	void deleteForumThread_ShouldReturn404_WhenForumThreadDoesNotExist() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(delete("/api/threads/9999")).andExpect(status().isNotFound());
	}

	@ForumThreadSql
	@Test
	@DisplayName("GET - /{forumThreadId} - Should return a forum thread by ID")
	void getForumThread_ShouldReturnForumThreadById() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(get("/api/threads/1"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(1), jsonPath("$.title").value("Favorite Apps"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /{forumThreadId} - Should return 404 when forum thread does not exist")
	void getForumThread_ShouldReturn404_WhenForumThreadDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/threads/9999")).andExpect(status().isNotFound());
	}

}
