package source.code.integration.test.controller.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import source.code.dto.request.activity.DailyActivitiesGetDto;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.request.activity.DailyActivityItemUpdateDto;
import source.code.integration.containers.MySqlRedisContainers;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;


import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@TestPropertySource(properties = "schema.name=activity")
public class DailyActivityTest extends MySqlRedisContainers {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ActivitySql
    @Test
    @DisplayName("POST - / - Should return all daily activities for the user")
    void getAllDailyActivitiesByUser() throws Exception {
        Utils.setUserContext(1);

        DailyActivitiesGetDto request = new DailyActivitiesGetDto();
        request.setDate(LocalDate.of(2023, 10, 5));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.activities").isArray(),
                        jsonPath("$.activities.length()").value(2),
                        jsonPath("$.totalCaloriesBurned").isNumber()
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - / - Should return empty list when no daily activity items exist in existing daily cart")
    void getAllDailyActivitiesByUserEmpty() throws Exception {
        Utils.setUserContext(1);

        DailyActivitiesGetDto request = new DailyActivitiesGetDto();
        request.setDate(LocalDate.of(2023, 10, 6));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.activities").isArray(),
                        jsonPath("$.activities.length()").value(0),
                        jsonPath("$.totalCaloriesBurned").value(0)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - / - Should return empty list when daily cart didn't exist for the date")
    void getAllDailyActivitiesByUserNotFound() throws Exception {
        Utils.setUserContext(1);

        DailyActivitiesGetDto request = new DailyActivitiesGetDto();
        request.setDate(LocalDate.of(2023, 10, 2));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.activities").isArray(),
                        jsonPath("$.activities.length()").value(0),
                        jsonPath("$.totalCaloriesBurned").value(0)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - /add/{activityId} - Should add a daily activity to the user's cart")
    void addDailyActivityToUser() throws Exception {
        Utils.setUserContext(1);

        DailyActivityItemCreateDto request = new DailyActivityItemCreateDto();
        request.setTime(30);
        request.setDate(LocalDate.of(2023, 10, 6));

        mockMvc.perform(post("/api/daily-activities/add/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        DailyActivitiesGetDto verifyRequest = new DailyActivitiesGetDto();
        verifyRequest.setDate(LocalDate.of(2023, 10, 6));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpectAll(
                        jsonPath("$.activities.length()").value(1),
                        jsonPath("$.activities[0].id").value(3),
                        jsonPath("$.activities[0].time").value(30)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - /add/{activityId} - Should increase time if activity already exists")
    void addDailyActivityToUserAlreadyExists() throws Exception {
        Utils.setUserContext(1);

        DailyActivityItemCreateDto request = new DailyActivityItemCreateDto();
        request.setTime(20);
        request.setDate(LocalDate.of(2023, 10, 5));

        mockMvc.perform(post("/api/daily-activities/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        DailyActivitiesGetDto verifyRequest = new DailyActivitiesGetDto();
        verifyRequest.setDate(LocalDate.of(2023, 10, 5));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpectAll(
                        jsonPath("$.activities[?(@.id == 1)].time").value(50),
                        jsonPath("$.activities[?(@.id == 2)].time").value(45)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("POST - /add/{activityId} - Should return 404 when activity does not exist")
    void addDailyActivityToUserNotFound() throws Exception {
        Utils.setUserContext(1);

        DailyActivityItemCreateDto request = new DailyActivityItemCreateDto();
        request.setTime(30);
        request.setDate(LocalDate.of(2023, 10, 6));

        mockMvc.perform(post("/api/daily-activities/add/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /modify-activity/{activityId} - Should update daily activity item")
    void updateDailyCartActivity() throws Exception {
        Utils.setUserContext(1);

        DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto();
        updateDto.setTime(40);

        mockMvc.perform(patch("/api/daily-activities/modify-activity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        DailyActivitiesGetDto verifyRequest = new DailyActivitiesGetDto();
        verifyRequest.setDate(LocalDate.of(2023, 10, 5));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpectAll(
                        jsonPath("$.activities[?(@.id == 1)].time").value(40),
                        jsonPath("$.activities[?(@.id == 2)].time").value(45)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /modify-activity/{activityId} - Should return 404 when daily activity item does not exist")
    void updateDailyCartActivityNotFound() throws Exception {
        Utils.setUserContext(1);

        DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto();

        mockMvc.perform(patch("/api/daily-activities/modify-activity/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /modify-activity/{activityId} - Should return 400 when patch is invalid")
    void updateDailyCartActivityInvalidPatch() throws Exception {
        Utils.setUserContext(1);

        DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto();
        updateDto.setTime(-10);

        mockMvc.perform(patch("/api/daily-activities/modify-activity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @ActivitySql
    @Test
    @DisplayName("PATCH - /modify-activity/{activityId} - Should return 403 when user is not owner")
    void updateNotOwnerAdmin() throws Exception {
        Utils.setUserContext(2);

        DailyActivityItemUpdateDto updateDto = new DailyActivityItemUpdateDto();
        updateDto.setTime(40);

        mockMvc.perform(patch("/api/daily-activities/modify-activity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @ActivitySql
    @Test
    @DisplayName("DELETE - /remove/{dailyActivityItemId} - Should delete")
    void removeActivityFromDailyCartActivity() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/daily-activities/remove/1"))
                .andExpect(status().isNoContent());

        DailyActivitiesGetDto verifyRequest = new DailyActivitiesGetDto();
        verifyRequest.setDate(LocalDate.of(2023, 10, 5));

        mockMvc.perform(post("/api/daily-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpectAll(
                        jsonPath("$.activities.length()").value(1),
                        jsonPath("$.activities[0].id").value(2)
                );
    }

    @ActivitySql
    @Test
    @DisplayName("DELETE - /remove/{dailyActivityItemId} - Should return 404 when daily activity item does not exist")
    void removeActivityFromDailyCartActivityNotFound() throws Exception {
        Utils.setUserContext(1);

        mockMvc.perform(delete("/api/daily-activities/remove/999"))
                .andExpect(status().isNotFound());
    }

    @ActivitySql
    @Test
    @DisplayName("DELETE - /remove/{dailyActivityItemId} - Should return 403 when user is not owner")
    void removeNotOwnerAdmin() throws Exception {
        Utils.setUserContext(2);

        mockMvc.perform(delete("/api/daily-activities/remove/1"))
                .andExpect(status().isForbidden());
    }
}
