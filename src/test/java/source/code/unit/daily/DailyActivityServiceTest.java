package source.code.unit.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.activity.DailyActivityItem;
import source.code.model.daily.DailyCart;
import source.code.model.user.profile.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityItemRepository;
import source.code.repository.DailyCartRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.daily.DailyActivityServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyActivityServiceTest {
    private static final int USER_ID = 1;
    private static final int ACTIVITY_ID = 1;
    private static final int INVALID_ACTIVITY_ID = 999;

    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ValidationService validationService;
    @Mock
    private DailyActivityMapper dailyActivityMapper;
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private DailyCartRepository dailyCartRepository;
    @Mock
    private DailyActivityItemRepository dailyActivityItemRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DailyActivityServiceImpl dailyActivityService;

    private Activity activity;
    private DailyCart dailyCart;
    private DailyActivityItem dailyActivityItem;
    private DailyActivityItemCreateDto createDto;
    private JsonMergePatch patch;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        activity = new Activity();
        dailyCart = new DailyCart();
        dailyActivityItem = new DailyActivityItem();
        createDto = new DailyActivityItemCreateDto();
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);

        activity.setId(1);
        dailyCart.setId(1);
        dailyActivityItem.setId(1);
        createDto.setTime(60);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void addActivityToDailyActivityItem_shouldUpdateExistingDailyActivityToDailyCartTime() {
        dailyCart.getDailyActivityItems().add(dailyActivityItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID))
                .thenReturn(activity);
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));

        dailyActivityService.addActivityToDailyCart(ACTIVITY_ID, createDto);

        verify(dailyCartRepository).save(dailyCart);
        assertEquals(dailyCart.getDailyActivityItems().get(0).getTime(), createDto.getTime());
    }

    @Test
    void addActivityToDailyActivityItem_shouldAddNewDailyActivityToDailyCart_whenNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID))
                .thenReturn(activity);
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.empty());

        dailyActivityService.addActivityToDailyCart(ACTIVITY_ID, createDto);

        verify(dailyCartRepository).save(dailyCart);
        assertEquals(dailyCart.getDailyActivityItems().get(0).getTime(), createDto.getTime());
    }

    @Test
    void addActivityToDailyActivityItem_shouldCreateNewDailyActivity_whenNotFound() {
        User user = new User();
        user.setId(USER_ID);
        DailyCart newDailyActivity = DailyCart.createForToday(user);
        newDailyActivity.setId(1);
        newDailyActivity.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());
        when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);
        when(dailyCartRepository.save(any(DailyCart.class)))
                .thenReturn(newDailyActivity);
        when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID))
                .thenReturn(activity);
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                anyInt(),
                eq(ACTIVITY_ID))
        ).thenReturn(Optional.empty());

        dailyActivityService.addActivityToDailyCart(ACTIVITY_ID, createDto);

        ArgumentCaptor<DailyCart> dailyActivityCaptor = ArgumentCaptor
                .forClass(DailyCart.class);
        verify(dailyCartRepository, times(2))
                .save(dailyActivityCaptor.capture());
        DailyCart savedDailyActivity = dailyActivityCaptor.getValue();
        assertEquals(USER_ID, savedDailyActivity.getUser().getId());
        assertEquals(
                createDto.getTime(),
                savedDailyActivity.getDailyActivityItems().get(0).getTime()
        );
    }

    @Test
    void removeActivityFromDailyCart_shouldRemoveExistingDailyActivityFromDailyCart() {
        dailyCart.getDailyActivityItems().add(dailyActivityItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));

        dailyActivityService.removeActivityFromDailyCart(ACTIVITY_ID);

        verify(dailyCartRepository).save(dailyCart);
        assertTrue(dailyCart.getDailyActivityItems().isEmpty());
    }

    @Test
    void removeActivityFromDailyCart_shouldThrowException_whenDailyActivityFromDailyCartNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.removeActivityFromDailyCart(ACTIVITY_ID)
        );
    }

    @Test
    void removeActivityFromDailyCart_shouldThrowException_whenDailyActivityNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.removeActivityFromDailyCart(ACTIVITY_ID)
        );
    }

    @Test
    void updateDailyActivityItem_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        DailyActivityItemCreateDto patchedDto = new DailyActivityItemCreateDto();
        patchedDto.setTime(120);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));
        doReturn(patchedDto).when(jsonPatchService).applyPatch(
                any(JsonMergePatch.class),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)
        );

        dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch);

        verify(validationService).validate(patchedDto);
        assertEquals(patchedDto.getTime(), dailyActivityItem.getTime());
        verify(dailyCartRepository).save(dailyCart);
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenDailyActivityItemNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenDailyActivityNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));
        doThrow(JsonPatchException.class).when(jsonPatchService).applyPatch(
                any(JsonMergePatch.class),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)
        );

        assertThrows(JsonPatchException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch)
        );

        verifyNoInteractions(validationService);
        verify(dailyCartRepository, never()).save(dailyCart);
    }
    @Test
    void updateDailyActivityItem_shouldThrowException_whenPatchValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        DailyActivityItemCreateDto patchedDto = new DailyActivityItemCreateDto();
        patchedDto.setTime(120);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityItemRepository.findByDailyCartIdAndActivityId(
                dailyCart.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));
        doReturn(patchedDto).when(jsonPatchService).applyPatch(
                any(JsonMergePatch.class),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)
        );

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch)
        );

        verify(validationService).validate(patchedDto);
        verify(dailyCartRepository, never()).save(dailyCart);
    }

    @Test
    void getActivitiesFromDailyCart_shouldReturnActivities() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(70);
        dailyCart.setUser(user);
        dailyCart.getDailyActivityItems().add(dailyActivityItem);

        ActivityCalculatedResponseDto calculatedResponseDto = new ActivityCalculatedResponseDto();
        calculatedResponseDto.setCaloriesBurned(100);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyActivityMapper.toActivityCalculatedResponseDto(
                dailyActivityItem,
                user.getWeight())
        ).thenReturn(calculatedResponseDto);

        DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyCart();

        assertEquals(1, result.getActivities().size());
        assertEquals(100, result.getTotalCaloriesBurned());
    }

    @Test
    void getActivitiesFromDailyCart_shouldReturnEmptyActivities_whenDailyCartNotFound() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(70);
        DailyCart newDailyActivity = DailyCart.createForToday(user);
        newDailyActivity.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());
        when(dailyCartRepository.save(any(DailyCart.class))).thenReturn(newDailyActivity);

        DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyCart();

        assertTrue(result.getActivities().isEmpty());
        assertEquals(0, result.getTotalCaloriesBurned());
    }
}
