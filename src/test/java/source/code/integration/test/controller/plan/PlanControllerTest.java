package source.code.integration.test.controller.plan;

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
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.request.plan.PlanUpdateDto;
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
public class PlanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PlanSql
    @Test
    @DisplayName("POST - / - Should create a new plan")
    void createPlan() throws Exception {
        Utils.setUserContext(1);
        PlanCreateDto createDto = new PlanCreateDto();
        createDto.setName("Test Plan");
        createDto.setDescription("A test plan description");
        createDto.setPlanTypeId(1);

        mockMvc.perform(post("/api/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Test Plan"),
                        jsonPath("$.description").value("A test plan description")
                );
    }

    @PlanSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing plan when owner")
    void updatePlan() throws Exception {
        Utils.setUserContext(1);
        PlanUpdateDto updateDto = new PlanUpdateDto();
        updateDto.setName("Updated Plan");

        mockMvc.perform(patch("/api/plans/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/plans/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("Updated Plan")
                );
    }

    @PlanSql
    @Test
    @DisplayName("PATCH - /{id} - Should update an existing plan when admin")
    void updatePlanAsAdmin() throws Exception {
        Utils.setAdminContext(5);
        PlanUpdateDto updateDto = new PlanUpdateDto();
        updateDto.setName("Updated Plan");

        mockMvc.perform(patch("/api/plans/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get("/api/plans/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("Updated Plan")
                );
    }

    @PlanSql
    @Test
    @DisplayName("PATCH - /{id} - Should return 403 when not owner or admin")
    void updatePlanForbidden() throws Exception {
        Utils.setUserContext(2);
        PlanUpdateDto updateDto = new PlanUpdateDto();
        updateDto.setName("Updated Plan");

        mockMvc.perform(patch("/api/plans/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH - /{id} - Should return 404 when plan not found")
    void updatePlanNotFound() throws Exception {
        Utils.setUserContext(1);
        PlanUpdateDto updateDto = new PlanUpdateDto();
        updateDto.setName("Updated Plan");

        mockMvc.perform(patch("/api/plans/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @PlanSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing plan when owner")
    void deletePlan() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(delete("/api/plans/1"))
                .andExpect(status().isNoContent());
    }

    @PlanSql
    @Test
    @DisplayName("DELETE - /{id} - Should delete an existing plan when admin")
    void deletePlanAsAdmin() throws Exception {
        Utils.setAdminContext(1);
        mockMvc.perform(delete("/api/plans/1"))
                .andExpect(status().isNoContent());
    }

    @PlanSql
    @Test
    @DisplayName("DELETE - /{id} - Should return 403 when not owner or admin")
    void deletePlanForbidden() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(delete("/api/plans/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE - /{id} - Should return 404 when plan not found")
    void deletePlanNotFound() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(delete("/api/plans/999"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @PlanSql
    @Test
    @DisplayName("GET - /{id} - Should retrieve an existing plan")
    void getPlan() throws Exception {
        mockMvc.perform(get("/api/plans/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1)
                );
    }

    @WithMockUser
    @Test
    @DisplayName("GET - /{id} - Should return 404 when plan not found")
    void getPlanNotFound() throws Exception {
        mockMvc.perform(get("/api/plans/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @PlanSql
    @Test
    @DisplayName("GET - /{id} - Should return 403 when not owner or admin and plan is private")
    void getPrivatePlanForbidden() throws Exception {
        Utils.setUserContext(2);
        mockMvc.perform(get("/api/plans/13")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @PlanSql
    @Test
    @DisplayName("GET - /private/{isPrivate} - Should retrieve all public plans when isPrivate is null")
    void getAllPlans() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/plans/private"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(12)),
                        jsonPath("$.page.totalElements").value(12)
                );
    }

    @PlanSql
    @Test
    @DisplayName("GET - /private/{isPrivate} - Should retrieve all public plans when isPrivate is false")
    void getAllPublicPlans() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/plans/private/false"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(12)),
                        jsonPath("$.page.totalElements").value(12)
                );
    }

    @PlanSql
    @Test
    @DisplayName("GET - /private/{isPrivate} - Should retrieve all private plans when isPrivate is true")
    void getAllPrivatePlans() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/plans/private/true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(3)),
                        jsonPath("$.page.totalElements").value(3)
                );
    }

    @Test
    @DisplayName("GET - / - Should return empty page when no plans exist")
    void getAllPlansEmpty() throws Exception {
        Utils.setUserContext(1);
        mockMvc.perform(get("/api/plans/private")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(0)),
                        jsonPath("$.page.totalElements").value(0)
                );
    }

    @WithMockUser
    @PlanSql
    @Test
    @DisplayName("PATCH - /{id}/view - Should increment views for a plan")
    void incrementViews() throws Exception {
        mockMvc.perform(patch("/api/plans/1/view"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @PlanSql
    @Test
    @DisplayName("PATCH - /{id}/view - Should increment views multiple times")
    void incrementViewsMultipleTimes() throws Exception {
        mockMvc.perform(patch("/api/plans/1/view"))
                .andExpect(status().isNoContent());
        mockMvc.perform(patch("/api/plans/1/view"))
                .andExpect(status().isNoContent());
        mockMvc.perform(patch("/api/plans/1/view"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @Test
    @DisplayName("PATCH - /{id}/view - Should return 204 even for non-existent plan")
    void incrementViewsNonExistentPlan() throws Exception {
        mockMvc.perform(patch("/api/plans/999/view"))
                .andExpect(status().isNoContent());
    }
}
