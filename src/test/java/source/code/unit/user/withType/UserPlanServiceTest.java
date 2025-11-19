package source.code.unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.model.plan.Plan;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserPlan;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.user.interaction.withType.UserPlanServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPlanServiceTest {
    @Mock
    private UserPlanRepository userPlanRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlanMapper planMapper;
    @Mock
    private AwsS3Service awsS3Service;
    @InjectMocks
    private UserPlanServiceImpl userPlanService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("saveToUser - Should save to user with type")
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        User user = new User();
        Plan plan = new Plan();
        plan.setIsPublic(true);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        userPlanService.saveToUser(planId, type);

        verify(userPlanRepository).save(any(UserPlan.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if plan is private")
    public void saveToUser_ShouldThrowNotSupportedInteractionTypeExceptionIfPlanIsPrivate() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        User user = new User();
        Plan plan = new Plan();
        plan.setIsPublic(false);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        assertThrows(NotSupportedInteractionTypeException.class,
                () -> userPlanService.saveToUser(planId, type));

        verify(userPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userPlanService.saveToUser(planId, type));

        verify(userPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userPlanService.saveToUser(planId, type));

        verify(userPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if plan not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfPlanNotFound() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userPlanService.saveToUser(planId, type));

        verify(userPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        UserPlan userPlan = UserPlan.createWithUserPlanType(new User(), new Plan(), type);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.findByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(Optional.of(userPlan));

        userPlanService.deleteFromUser(planId, type);

        verify(userPlanRepository).delete(userPlan);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if user plan not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserPlanNotFound() {
        int userId = 1;
        int planId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.findByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userPlanService.deleteFromUser(planId, type));

        verify(userPlanRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all plans by type")
    public void getAllFromUser_ShouldReturnAllPlansByType() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setId(2);

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(List.of(dto1, dto2));

        var result = userPlanService.getAllFromUser(userId, type, "DESC");

        assertEquals(2, result.size());
        verify(userPlanRepository).findPlanSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no plans")
    public void getAllFromUser_ShouldReturnEmptyListIfNoPlans() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userPlanService.getAllFromUser(userId, type, "DESC");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int planId = 100;
        long saveCount = 5;
        long likeCount = 10;
        Plan plan = new Plan();
        TypeOfInteraction saveType = TypeOfInteraction.SAVE;
        TypeOfInteraction likeType = TypeOfInteraction.LIKE;

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(userPlanRepository.countByPlanIdAndType(planId, saveType))
                .thenReturn(saveCount);
        when(userPlanRepository.countByPlanIdAndType(planId, likeType))
                .thenReturn(likeCount);

        var result = userPlanService.calculateLikesAndSaves(planId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(planRepository).findById(planId);
        verify(userPlanRepository).countByPlanIdAndType(planId, saveType);
        verify(userPlanRepository).countByPlanIdAndType(planId, likeType);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if plan not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfPlanNotFound() {
        int planId = 100;

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userPlanService.calculateLikesAndSaves(planId));

        verify(userPlanRepository, never()).countByPlanIdAndType(anyInt(), any());
    }

    @Test
    @DisplayName("getAllFromUser with type and sortDirection DESC - Should sort by interaction date DESC")
    public void getAllFromUser_WithType_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, "DESC");

        assertSortedResult(result, 2, 2, 1);
        verify(userPlanRepository).findPlanSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type and sortDirection ASC - Should sort by interaction date ASC")
    public void getAllFromUser_WithType_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto2, dto1)));

        List<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, "ASC");

        assertSortedResult(result, 2, 1, 2);
        verify(userPlanRepository).findPlanSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type default - Should sort DESC when no direction specified")
    public void getAllFromUser_WithType_DefaultShouldSortDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, "DESC");

        assertSortedResult(result, 2, 2, 1);
        verify(userPlanRepository).findPlanSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type - Should handle null dates properly")
    public void getAllFromUser_WithType_ShouldHandleNullDates() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        PlanSummaryDto dto1 = createPlanSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        PlanSummaryDto dto2 = createPlanSummaryDto(2, null);
        PlanSummaryDto dto3 = createPlanSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2, dto3)));

        List<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, "DESC");

        assertSortedResult(result, 3, 3, 1, 2);
        verify(userPlanRepository).findPlanSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type - Should populate image URLs after sorting")
    public void getAllFromUser_WithType_ShouldPopulateImageUrlsAfterSorting() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        dto1.setImageName("image1.jpg");
        dto1.setAuthorImageUrl("author1.jpg");
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);
        dto2.setImageName("image2.jpg");
        dto2.setAuthorImageUrl("author2.jpg");

        when(userPlanRepository.findPlanSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));
        when(awsS3Service.getImage("image1.jpg")).thenReturn("https://s3.com/image1.jpg");
        when(awsS3Service.getImage("image2.jpg")).thenReturn("https://s3.com/image2.jpg");
        when(awsS3Service.getImage("author1.jpg")).thenReturn("https://s3.com/author1.jpg");
        when(awsS3Service.getImage("author2.jpg")).thenReturn("https://s3.com/author2.jpg");

        List<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, "DESC");

        assertNotNull(result);
        assertEquals(2, result.size());
        PlanSummaryDto first = (PlanSummaryDto) result.get(0);
        PlanSummaryDto second = (PlanSummaryDto) result.get(1);
        assertEquals("https://s3.com/image2.jpg", first.getFirstImageUrl());
        assertEquals("https://s3.com/image1.jpg", second.getFirstImageUrl());
        assertEquals("https://s3.com/author2.jpg", first.getAuthorImageUrl());
        assertEquals("https://s3.com/author1.jpg", second.getAuthorImageUrl());
        verify(awsS3Service).getImage("image1.jpg");
        verify(awsS3Service).getImage("image2.jpg");
        verify(awsS3Service).getImage("author1.jpg");
        verify(awsS3Service).getImage("author2.jpg");
    }

    private PlanSummaryDto createPlanSummaryDto(int id, LocalDateTime interactionDate) {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setId(id);
        dto.setInteractedWithAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((PlanSummaryDto) result.get(i)).getId());
        }
    }
}