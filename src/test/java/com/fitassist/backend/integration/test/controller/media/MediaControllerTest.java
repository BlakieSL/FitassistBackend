package com.fitassist.backend.integration.test.controller.media;

import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.config.TestAwsS3Config;
import com.fitassist.backend.integration.containers.AwsS3ContainerInitializer;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.integration.utils.Utils;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockRedisConfig.class, MockAwsSesConfig.class, TestAwsS3Config.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class, AwsS3ContainerInitializer.class })
public class MediaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@WithMockUser
	@MediaSql
	@Test
	@DisplayName("GET - /all/{parentId}/{parentType} - Should return all media for a parent")
	public void getAllMediaForParent() throws Exception {
		mockMvc.perform(get("/api/media/all/1/FOOD"))
			.andExpectAll(status().isOk(), jsonPath("$[0].imageUrl").isNotEmpty(), jsonPath("$[0].parentId").value(1),
					jsonPath("$[0].parentType").value("FOOD"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /all/{parentId}/{parentType} - Should return empty list when no media for parent entity")
	public void getAllMediaForNonExistingParent() throws Exception {
		mockMvc.perform(get("/api/media/all/999/FOOD"))
			.andExpectAll(status().isOk(), jsonPath("$").isArray(), jsonPath("$.length()").value(0));
	}

	@WithMockUser
	@MediaSql
	@Test
	@DisplayName("GET - /first/{parentId}/{parentType} - Should return first media for a parent")
	public void getFirstMediaForParent() throws Exception {
		mockMvc.perform(get("/api/media/first/1/FOOD"))
			.andExpectAll(status().isOk(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.parentId").value(1),
					jsonPath("$.parentType").value("FOOD"));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /first/{parentId}/{parentType} - Should return 404 when no media for parent entity")
	public void getFirstMediaForNonExistingParent() throws Exception {
		mockMvc.perform(get("/api/media/first/999/FOOD")).andExpectAll(status().isNotFound());
	}

	@WithMockUser
	@MediaSql
	@Test
	@DisplayName("GET - /{mediaId} - Should return media by ID")
	public void getMediaById() throws Exception {
		mockMvc.perform(get("/api/media/1"))
			.andExpectAll(status().isOk(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.id").value(1));
	}

	@WithMockUser
	@Test
	@DisplayName("GET - /{mediaId} - Should return 404 when media does not exist")
	public void getMediaByNonExistingId() throws Exception {
		mockMvc.perform(get("/api/media/999")).andExpectAll(status().isNotFound());
	}

	@Test
	@DisplayName("POST - / - Should create media, when user is admin")
	public void createMedia() throws Exception {
		Utils.setAdminContext(1);
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", "fake".getBytes());
		String parentType = MediaConnectedEntity.FOOD.name();
		String parentId = "1";

		mockMvc
			.perform(
					multipart("/api/media").file(mockImage).param("parentType", parentType).param("parentId", parentId))
			.andExpectAll(status().isCreated(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.parentId").value(1),
					jsonPath("$.parentType").value("FOOD"));
	}

	@MediaSql
	@Test
	@DisplayName("POST - / - Should create media, when user is owner")
	public void createMediaAsOwner() throws Exception {
		Utils.setUserContext(1);
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", "fake".getBytes());
		String parentType = MediaConnectedEntity.RECIPE.name();
		String parentId = "1";

		mockMvc
			.perform(
					multipart("/api/media").file(mockImage).param("parentType", parentType).param("parentId", parentId))
			.andExpectAll(status().isCreated(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.parentId").value(1),
					jsonPath("$.parentType").value("RECIPE"));
	}

	@MediaSql
	@Test
	@DisplayName("POST - / - Should return 403 when user is not owner or admin")
	public void createMediaWithoutPermission() throws Exception {
		Utils.setUserContext(1);
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpeg",
				"fake-image-content".getBytes());
		String parentType = MediaConnectedEntity.FOOD.name();
		String parentId = "1";

		mockMvc
			.perform(
					multipart("/api/media").file(mockImage).param("parentType", parentType).param("parentId", parentId))
			.andExpectAll(status().isForbidden());
	}

	@MediaSql
	@Test
	@DisplayName("DELETE - /{mediaId} - Should delete media when user is owner")
	public void deleteMediaAsOwner() throws Exception {
		Utils.setUserContext(1);
		mockMvc.perform(delete("/api/media/3")).andExpectAll(status().isNoContent());
	}

	@MediaSql
	@Test
	@DisplayName("DELETE - /{mediaId} - Should delete media when user is admin")
	public void deleteMediaAsAdmin() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/media/1")).andExpectAll(status().isNoContent());
	}

	@MediaSql
	@Test
	@DisplayName("DELETE - /{mediaId} - Should return 403 when user is not owner or admin")
	public void deleteMediaWithoutPermission() throws Exception {
		Utils.setUserContext(2);
		mockMvc.perform(delete("/api/media/1")).andExpectAll(status().isForbidden());
	}

	@MediaSql
	@Test
	@DisplayName("DELETE - /{mediaId} - Should return 404 when media does not exist")
	public void deleteNonExistingMedia() throws Exception {
		Utils.setAdminContext(1);
		mockMvc.perform(delete("/api/media/999")).andExpectAll(status().isNotFound());
	}

	@Test
	@DisplayName("POST - / - Should create user profile image when user has no existing image")
	public void createUserProfileImageWhenNoExistingImage() throws Exception {
		Utils.setUserContext(1);
		MockMultipartFile mockImage = new MockMultipartFile("image", "profile.jpg", "image/jpeg", "fake".getBytes());

		mockMvc
			.perform(multipart("/api/media").file(mockImage)
				.param("parentType", MediaConnectedEntity.USER.name())
				.param("parentId", "1"))
			.andExpectAll(status().isCreated(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.parentId").value(1),
					jsonPath("$.parentType").value("USER"));
	}

	@MediaSql
	@Test
	@DisplayName("POST - / - Should replace existing user profile image when uploading a new one")
	public void createSecondUserProfileImageShouldReplaceOldOne() throws Exception {
		Utils.setUserContext(2);
		MockMultipartFile mockImage = new MockMultipartFile("image", "new-profile.jpg", "image/jpeg",
				"fake".getBytes());

		mockMvc
			.perform(multipart("/api/media").file(mockImage)
				.param("parentType", MediaConnectedEntity.USER.name())
				.param("parentId", "2"))
			.andExpectAll(status().isCreated(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.parentId").value(2),
					jsonPath("$.parentType").value("USER"));
	}

	@MediaSql
	@Test
	@DisplayName("POST - / - Should allow multiple images for non-user entities")
	public void createMultipleImagesForNonUserEntity() throws Exception {
		Utils.setAdminContext(1);
		MockMultipartFile mockImage = new MockMultipartFile("image", "food2.jpg", "image/jpeg", "fake".getBytes());

		mockMvc
			.perform(multipart("/api/media").file(mockImage)
				.param("parentType", MediaConnectedEntity.FOOD.name())
				.param("parentId", "1"))
			.andExpectAll(status().isCreated(), jsonPath("$.imageUrl").isNotEmpty(), jsonPath("$.parentId").value(1),
					jsonPath("$.parentType").value("FOOD"));
	}

}
