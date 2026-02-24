package com.fitassist.backend.integration.test.controller.forumThread;

import tools.jackson.databind.ObjectMapper;
import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadUpdateDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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
		ForumThreadCreateDto createDto = new ForumThreadCreateDto("New Thread Title",
				"This is the content of the new thread", 1);

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
		ForumThreadUpdateDto updateDto = new ForumThreadUpdateDto("Updated title", "Updated content", null);

		mockMvc
			.perform(patch("/api/threads/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk());
	}

	@ForumThreadSql
	@Test
	@DisplayName("PATCH - /{forumThreadId} - Should update a forum thread, when user is admin")
	void updateForumThread_ShouldUpdateForumThread_WhenUserIsAdmin() throws Exception {
		Utils.setAdminContext(2);
		ForumThreadUpdateDto updateDto = new ForumThreadUpdateDto("Updated title", "Updated content", null);

		mockMvc
			.perform(patch("/api/threads/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk());
	}

	@ForumThreadSql
	@Test
	@DisplayName("PATCH - /{forumThreadId} - Should return 403 when user is not owner or admin")
	void updateForumThread_ShouldReturn403_WhenUserIsNotOwnerOrAdmin() throws Exception {
		Utils.setUserContext(3);
		ForumThreadUpdateDto updateDto = new ForumThreadUpdateDto("Updated title", "Updated content", null);

		mockMvc
			.perform(patch("/api/threads/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
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
