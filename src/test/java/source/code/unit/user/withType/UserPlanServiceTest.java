package source.code.unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.implementation.user.interaction.withType.UserPlanServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ImageUrlPopulationService imageUrlPopulationService;
    @Mock
    private SortingService sortingService;
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
    public void getAllFromUser_ShouldReturnAllPlansByType() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setId(2);

        Page<PlanSummaryDto> page = new PageImpl<>(List.of(dto1, dto2));
        when(planRepository.findPlanSummaryUnified(eq(userId), eq(type), eq(true), isNull(), any(Pageable.class)))
                .thenReturn(page);

        Page<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(planRepository).findPlanSummaryUnified(eq(userId), eq(type), eq(true), isNull(), any(Pageable.class));
    }

    @Test
    public void getAllFromUser_ShouldReturnEmptyListIfNoPlans() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PlanSummaryDto> page = new PageImpl<>(List.of());
        when(planRepository.findPlanSummaryUnified(eq(userId), eq(type), eq(true), isNull(), any(Pageable.class)))
                .thenReturn(page);

        Page<BaseUserEntity> result = userPlanService.getAllFromUser(userId, type, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
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
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfPlanNotFound() {
        int planId = 100;

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userPlanService.calculateLikesAndSaves(planId));

        verify(userPlanRepository, never()).countByPlanIdAndType(anyInt(), any());
    }
}
