package source.code.integration.test.controller.complaint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.helper.Enum.model.ComplaintSubClass;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;
import source.code.model.complaint.ComplaintReason;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestSetup
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
public class ComplaintControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ComplaintSql
    @Test
    @DisplayName("POST - / - Should create a thread complaint")
    public void createThreadComplaint() throws Exception {
        Utils.setUserContext(1);
        ComplaintCreateDto requestBody = new ComplaintCreateDto();
        requestBody.setReason(ComplaintReason.INAPPROPRIATE_CONTENT);
        requestBody.setParentId(1);
        requestBody.setSubClass(ComplaintSubClass.THREAD_COMPLAINT);


        mockMvc.perform(post("/api/complaint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpectAll(
                        status().isCreated()
                );
    }

    @ComplaintSql
    @Test
    @DisplayName("POST - / - Should create a comment complaint")
    public void createComplaint() throws Exception {
        Utils.setUserContext(1);
        ComplaintCreateDto requestBody = new ComplaintCreateDto();
        requestBody.setReason(ComplaintReason.INAPPROPRIATE_CONTENT);
        requestBody.setParentId(1);
        requestBody.setSubClass(ComplaintSubClass.COMMENT_COMPLAINT);


        mockMvc.perform(post("/api/complaint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpectAll(
                        status().isCreated()
                );
    }

    @ComplaintSql
    @Test
    @DisplayName("PUT - /{complaintId}/resolve - Should resolve a complaint")
    public void resolveComplaint() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(put("/api/complaint/1/resolve"))
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @DisplayName("PUT - /{complaintId}/resolve - Should return 403 when not admin")
    public void resolveComplaintNotAdmin() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(put("/api/complaint/1/resolve"))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @ComplaintSql
    @Test
    @DisplayName("GET - /all - Should get all complaints")
    public void getAllComplaints() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(get("/api/complaint/all")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @DisplayName("GET - /all - Should return empty page when no complaints exist")
    public void getAllComplaintsEmptyPage() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(get("/api/complaint/all")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @DisplayName("GET - /all - Should return 403 when not admin")
    public void getAllComplaintsNotAdmin() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/complaint/all")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @ComplaintSql
    @Test
    @DisplayName("GET - /{complaintId} - Should get a complaint by id")
    public void getComplaintById() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(get("/api/complaint/1"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @DisplayName("GET - /{complaintId} - Should return 403  when not admin")
    public void getComplaintByIdNotAdmin() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/complaint/1"))
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @DisplayName("GET - /{complaintId} - Should return 404 when complaint does not exist")
    public void getComplaintByIdNotExist() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(get("/api/complaint/9999"))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}
