package source.code.integration.test.controller.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.AwsS3ContainerInitializer;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({MockRedisConfig.class, MockAwsSesConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=media")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class, AwsS3ContainerInitializer.class})
public class MediaTest {
    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @MediaSql
    @Test
    @DisplayName("GET - /all/{parentId}/{parentType} - Should return all media for a parent")
    public void getAllMediaForParent() throws Exception {
        mockMvc.perform(get("/api/media/all/1/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].imageUrl").isNotEmpty(),
                        jsonPath("$[0].parentId").value(1),
                        jsonPath("$[0].parentType").value("FOOD")
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /all/{parentId}/{parentType} - Should return empty list when no media for parent entity")
    public void getAllMediaForNonExistingParent() throws Exception {
        mockMvc.perform(get("/api/media/all/999/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(0)
                );
    }

    @WithMockUser
    @MediaSql
    @Test
    @DisplayName("GET - /first/{parentId}/{parentType} - Should return first media for a parent")
    public void getFirstMediaForParent() throws Exception {
        mockMvc.perform(get("/api/media/first/1/FOOD"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.imageUrl").isNotEmpty(),
                        jsonPath("$.parentId").value(1),
                        jsonPath("$.parentType").value("FOOD")
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /first/{parentId}/{parentType} - Should return 404 when no media for parent entity")
    public void getFirstMediaForNonExistingParent() throws Exception {
        mockMvc.perform(get("/api/media/first/999/FOOD"))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @WithMockUser
    @MediaSql
    @Test
    @DisplayName("GET - /{mediaId} - Should return media by ID")
    public void getMediaById() throws Exception {
        mockMvc.perform(get("/api/media/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.imageUrl").isNotEmpty(),
                        jsonPath("$.id").value(1)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{mediaId} - Should return 404 when media does not exist")
    public void getMediaByNonExistingId() throws Exception {
        mockMvc.perform(get("/api/media/999"))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @DisplayName("POST - / - Should create media, when user is admin")
    public void createMedia() throws Exception {
        Utils.setAdminContext(1);
        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        var parentType = MediaConnectedEntity.FOOD.name();
        var parentId = "1";

        mockMvc.perform(multipart("/api/media")
                .file(mockImage)
                .param("parentType", parentType)
                .param("parentId", parentId)
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.imageUrl").isNotEmpty(),
                jsonPath("$.parentId").value(1),
                jsonPath("$.parentType").value("FOOD")
        );
    }

    @MediaSql
    @Test
    @DisplayName("POST - / - Should create media, when user is owner")
    public void createMediaAsOwner() throws Exception {
        Utils.setUserContext(1);

        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        var parentType = MediaConnectedEntity.RECIPE.name();
        var parentId = "1";

        mockMvc.perform(multipart("/api/media")
                .file(mockImage)
                .param("parentType", parentType)
                .param("parentId", parentId)
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.imageUrl").isNotEmpty(),
                jsonPath("$.parentId").value(1),
                jsonPath("$.parentType").value("RECIPE")
        );
    }

    @MediaSql
    @Test
    @DisplayName("POST - / - Should return 403 when user is not owner or admin")
    public void createMediaWithoutPermission() throws Exception {
        Utils.setUserContext(1);
        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        var parentType = MediaConnectedEntity.FOOD.name();
        var parentId = "1";

        mockMvc.perform(multipart("/api/media")
                .file(mockImage)
                .param("parentType", parentType)
                .param("parentId", parentId)
        ).andExpectAll(
                status().isForbidden()
        );
    }

    @MediaSql
    @Test
    @DisplayName("DELETE - /{mediaId} - Should delete media when user is owner")
    public void deleteMediaAsOwner() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/media/3"))
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @MediaSql
    @Test
    @DisplayName("DELETE - /{mediaId} - Should delete media when user is admin")
    public void deleteMediaAsAdmin() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/media/1"))
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @MediaSql
    @Test
    @DisplayName("DELETE - /{mediaId} - Should return 403 when user is not owner or admin")
    public void deleteMediaWithoutPermission() throws Exception {
        Utils.setUserContext(2);

        mockMvc.perform(delete("/api/media/1"))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @MediaSql
    @Test
    @DisplayName("DELETE - /{mediaId} - Should return 404 when media does not exist")
    public void deleteNonExistingMedia() throws Exception {
        Utils.setAdminContext(1);

        mockMvc.perform(delete("/api/media/999"))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}
