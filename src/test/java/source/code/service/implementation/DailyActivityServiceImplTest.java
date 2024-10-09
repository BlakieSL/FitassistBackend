package source.code.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.DailyCartActivityCreateDto;
import source.code.helper.CalculationsHelper;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.model.*;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyActivityServiceImplTest {
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private CalculationsHelper calculationsHelper;
    @Mock
    private JsonPatchHelper jsonPatchHelper;
    @Mock
    private DailyActivityRepository dailyActivityRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DailyActivityServiceImpl dailyActivityService;

    private User user1;
    private User user2;
    private Activity activity1;
    private Activity activity2;
    private DailyActivity dailyActivity1;
    private DailyActivity dailyActivity2;
    private DailyCartActivity dailyCartActivity1;
    private DailyCartActivity dailyCartActivity2;
    private DailyCartActivityCreateDto createDto;
    @BeforeEach
    void setup() {
        user1 = createUser(1);
        user2 = createUser(2);

        activity1 = createActivity(1);
        activity2 = createActivity(2);

        dailyActivity1 = createDailyActivity(1, user1);
        dailyActivity2 = createDailyActivity(2, user2);

        dailyCartActivity1 = createDailyCartActivity(1, activity1, dailyActivity1);
        dailyCartActivity2 = createDailyCartActivity(2, activity2, dailyActivity2);

        dailyActivity1.getDailyCartActivities().add(dailyCartActivity1);
        dailyActivity2.getDailyCartActivities().add(dailyCartActivity2);

        createDto = createDailyCartActivityCreateDto(30);
    }

    private User createUser(int id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Activity createActivity(int id) {
        Activity activity = new Activity();
        activity.setId(id);
        return  activity;
    }

    private DailyActivity createDailyActivity(int id, User user) {
        DailyActivity dailyActivity = new DailyActivity();
        dailyActivity.setId(id);
        dailyActivity.setUser(user);
        return dailyActivity;
    }

    private DailyCartActivity createDailyCartActivity(int id, Activity activity, DailyActivity dailyActivity) {
        DailyCartActivity dailyCartActivity = new DailyCartActivity();
        dailyCartActivity.setId(id);
        dailyCartActivity.setActivity(activity);
        dailyCartActivity.setDailyActivity(dailyActivity);
        return dailyCartActivity;
    }

    private DailyCartActivityCreateDto createDailyCartActivityCreateDto(int time) {
        return new DailyCartActivityCreateDto(time);
    }

    @Test
    void updateDailyCarts_shouldUpdateActivities_whenActivitiesExist() {
        //Arrange
        LocalDate today = LocalDate.now();
        dailyActivity1.setDate(today);
        dailyActivity1.getDailyCartActivities().clear();

        dailyActivity2.setDate(today);
        dailyActivity2.getDailyCartActivities().clear();

        when(dailyActivityRepository.findAll()).thenReturn(List.of(dailyActivity1, dailyActivity2));

        // Act
        dailyActivityService.updateDailyCarts();

        // Assert
        verify(dailyActivityRepository, times(1)).findAll();
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        verify(dailyActivityRepository, times(1)).save(dailyActivity2);
        assertEquals(today, dailyActivity1.getDate());
        assertEquals(today, dailyActivity2.getDate());
        assertTrue(dailyActivity1.getDailyCartActivities().isEmpty());
        assertTrue(dailyActivity2.getDailyCartActivities().isEmpty());
    }

    @Test
    void updateDailyCarts_shouldDoNothing_whenNoActivitiesExist() {
        // Arrange
        when(dailyActivityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        dailyActivityService.updateDailyCarts();

        // Assert
        verify(dailyActivityRepository, times(1)).findAll();
        verify(dailyActivityRepository, never()).save(any(DailyActivity.class));
    }

    @Test
    void addActivityToDailyCartActivity_shouldAddActivityToDailyCartActivity_whenActivityDoesNotExistInDailyCart() {
        // Arrange
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));
        when(activityRepository.findById(activity1.getId())).thenReturn(Optional.of(activity1));

        // Act
        dailyActivityService.addActivityToDailyCartActivity(user1.getId(), activity1.getId(), createDto);

        // Assert
        verify(validationHelper, times(1)).validate(createDto);
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);

        assertEquals(1, dailyActivity1.getDailyCartActivities().size());

        DailyCartActivity addedActivity = dailyActivity1.getDailyCartActivities().get(0);
        assertEquals(activity1, addedActivity.getActivity());
        assertEquals(createDto.getTime(), addedActivity.getTime());
        assertEquals(dailyActivity1, addedActivity.getDailyActivity());
    }

    @Test
    void addActivityToDailyCartActivity_shouldUpdateTime_whenActivityExistsInDailyCart() {
        // Arrange
        int existingTime = 15;
        dailyCartActivity1.setTime(existingTime);

        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));
        when(activityRepository.findById(activity1.getId())).thenReturn(Optional.of(activity1));

        // Act
        dailyActivityService.addActivityToDailyCartActivity(user1.getId(), activity1.getId(), createDto);

        // Assert
        verify(validationHelper, times(1)).validate(createDto);
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertEquals(1, dailyActivity1.getDailyCartActivities().size());

        DailyCartActivity updatedActivity = dailyActivity1.getDailyCartActivities().get(0);
        assertEquals(activity1, updatedActivity.getActivity());
        assertEquals(createDto.getTime(), updatedActivity.getTime());
        assertEquals(dailyActivity1, updatedActivity.getDailyActivity());
    }

    @Test
    void addActivityToDailyCartActivity_shouldThrowException_whenValidationFails() {
        // Arrange
        doThrow(new IllegalArgumentException("Validation failed")).when(validationHelper).validate(createDto);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                dailyActivityService.addActivityToDailyCartActivity(user1.getId(), activity1.getId(), createDto));

        assertEquals("Validation failed", exception.getMessage());
        verify(validationHelper, times(1)).validate(createDto);
        verify(dailyActivityRepository, never()).save(any());
    }

    @Test
    void addActivityToDailyCartActivity_shouldThrowException_whenActivityNotFound() {
        // Arrange
        dailyActivity1.getDailyCartActivities().clear();
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));
        when(activityRepository.findById(activity1.getId())).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.addActivityToDailyCartActivity(user1.getId(), activity1.getId(), createDto));

        assertEquals("Activity with id: " + activity1.getId() + " not found", exception.getMessage());
        verify(validationHelper, times(1)).validate(createDto);
        verify(activityRepository, times(1)).findById(activity1.getId());
        verify(dailyActivityRepository, never()).save(any());
    }

    @Test
    void removeActivityFromDailyActivity_shouldRemoveDailyCartActivity_whenDailyCartActivityFound() {
        // Arrange
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));

        // Act
        dailyActivityService.removeActivityFromDailyActivity(user1.getId(), activity1.getId());

        // Assert
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertTrue(dailyActivity1.getDailyCartActivities().isEmpty());
    }

    @Test
    void removeActivityFromDailyActivity_shouldThrowException_whenDailyCartActivityNotFound() {
        // Arrange
        int nonExistentActivityId = 11;
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.removeActivityFromDailyActivity(user1.getId(), nonExistentActivityId)
        );

        assertEquals("Activity with id: " + nonExistentActivityId + " not found", exception.getMessage());
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(dailyActivityRepository, never()).save(any());
    }

    @Test
    void updateDailyCartActivity_shouldUpdateDailyCartActivity_whenPatched() throws JsonPatchException, JsonProcessingException {
        // Arrange
        int newTime = 30;
        dailyCartActivity1.setTime(20);

        DailyCartActivityCreateDto patchedDto = new DailyCartActivityCreateDto();
        patchedDto.setTime(newTime);

        JsonMergePatch patch = mock(JsonMergePatch.class);
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));
        when(jsonPatchHelper.applyPatch(eq(patch), any(DailyCartActivityCreateDto.class), eq(DailyCartActivityCreateDto.class)))
                .thenReturn(patchedDto);

        // Act
        dailyActivityService.updateDailyCartActivity(user1.getId(), activity1.getId(), patch);

        // Assert
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(jsonPatchHelper, times(1)).applyPatch(eq(patch), any(DailyCartActivityCreateDto.class), eq(DailyCartActivityCreateDto.class));
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertEquals(newTime, dailyCartActivity1.getTime());
    }
}
