package source.code.integration.test.controller.comment;

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
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.comment.CommentUpdateDto;
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
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @CommentSql
    @Test
    @DisplayName("POST - / - Should create a comment")
    public void createComment() throws Exception {
        Utils.setUserContext(1);
        CommentCreateDto requestBody = new CommentCreateDto();
        requestBody.setText("This is a test comment");
        requestBody.setThreadId(1);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpectAll(status().isCreated());
    }

    @CommentSql
    @Test
    @DisplayName("PATCH - /{commentId} - Should update a comment when admin")
    public void updateComment() throws Exception {
        Utils.setAdminContext(1);
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Updated comment text");

        mockMvc.perform(patch("/api/comments/1")
                .content(objectMapper.writeValueAsString(updateDto))
                .contentType("application/merge-patch+json")
        ).andExpectAll(
                status().isNoContent()
        );

    }

    @CommentSql
    @Test
    @DisplayName("PATCH - /{commentId} - Should update a comment when owner")
    public void updateCommentAsOwner() throws Exception {
        Utils.setUserContext(2);
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Updated comment text");

        mockMvc.perform(patch("/api/comments/1")
                .content(objectMapper.writeValueAsString(updateDto))
                .contentType("application/merge-patch+json")
        ).andExpectAll(
                status().isNoContent()
        );
    }

    @Test
    @DisplayName("PATCH - /{commentId} - Should return 404 when comment does not exist")
    public void updateCommentNotFound() throws Exception {
        Utils.setUserContext(1);
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Updated comment text");

        mockMvc.perform(patch("/api/comments/999")
                .content(objectMapper.writeValueAsString(updateDto))
                .contentType("application/merge-patch+json")
        ).andExpectAll(status().isNotFound());
    }

    @CommentSql
    @Test
    @DisplayName("PATCH - /{commentId} - Should return 403 when not owner or admin")
    public void updateCommentNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Updated comment text");

        mockMvc.perform(patch("/api/comments/1")
                .content(objectMapper.writeValueAsString(updateDto))
                .contentType("application/merge-patch+json")
        ).andExpectAll(
                status().isForbidden()
        );
    }

    @CommentSql
    @Test
    @DisplayName("DELETE - /{commentId} - Should delete a comment when admin")
    public void deleteComment() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(delete("/api/comments/1"))
                .andExpectAll(status().isNoContent());
    }

    @CommentSql
    @Test
    @DisplayName("DELETE - /{commentId} - Should delete a comment when owner")
    public void deleteCommentAsOwner() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(delete("/api/comments/1"))
                .andExpectAll(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE - /{commentId} - Should return 404 when comment does not exist")
    public void deleteCommentNotFound() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(delete("/api/comments/999"))
                .andExpectAll(status().isNotFound());
    }

    @CommentSql
    @Test
    @DisplayName("DELETE - /{commentId} - Should return 403 when not owner or admin")
    public void deleteCommentNotOwnerOrAdmin() throws Exception {
        Utils.setUserContext(3);
        mockMvc.perform(delete("/api/comments/1"))
                .andExpectAll(status().isForbidden());
    }

    @WithMockUser
    @CommentSql
    @Test
    @DisplayName("GET - /{commentId} - Should get a comment")
    public void getComment() throws Exception {
        mockMvc.perform(get("/api/comments/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.text").value("I really like MyFitnessPal for tracking")
                );
    }

    @WithMockUser
    @CommentSql
    @Test
    @DisplayName("GET - /{commentId} - Should return 404 when comment does not exist")
    public void getCommentNotFound() throws Exception {
        mockMvc.perform(get("/api/comments/999"))
                .andExpectAll(status().isNotFound());
    }

    @WithMockUser
    @CommentSql
    @Test
    @DisplayName("GET - /top/{threadId} - Should get top comments for thread")
    public void getTopCommentsForThread() throws Exception {
        mockMvc.perform(get("/api/comments/top/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[0].text").value("I really like MyFitnessPal for tracking")
                );
    }

    @WithMockUser
    @CommentSql
    @Test
    @DisplayName("GET - /top/{threadId} - Should return empty page when thread does not exist")
    public void getTopCommentsForThreadNotFound() throws Exception {
        mockMvc.perform(get("/api/comments/top/999"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isEmpty()
                );
    }

    @WithMockUser
    @CommentSql
    @Test
    @DisplayName("GET - /replies/{commentId} - Should get replies for comment")
    public void getReplies() throws Exception {
        mockMvc.perform(get("/api/comments/replies/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty(),
                        jsonPath("$[0].replies", hasSize(1)),
                        jsonPath("$[0].replies.[0].replies", hasSize(1))
                );
    }

    @WithMockUser
    @CommentSql
    @Test
    @DisplayName("GET - /replies/{commentId} - Should return empty list when comment does not exist")
    public void getRepliesNotFound() throws Exception {
        mockMvc.perform(get("/api/comments/replies/999"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );
    }
}
