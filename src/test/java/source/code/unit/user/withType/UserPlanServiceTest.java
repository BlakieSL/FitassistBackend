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
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.model.plan.Plan;
import source.code.model.user.UserPlan;
import source.code.model.user.User;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withType.UserPlanServiceImpl;

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
        short type = 1;
        User user = new User();
        Plan plan = new Plan();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        userPlanService.saveToUser(planId, type);

        verify(userPlanRepository).save(any(UserPlan.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int planId = 100;
        short type = 1;

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
        short type = 1;

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
        short type = 1;
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
        short type = 1;
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
        short type = 1;

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
        short type = 1;
        UserPlan plan1 = UserPlan.createWithUserPlanType(new User(), new Plan(), type);
        UserPlan plan2 = UserPlan.createWithUserPlanType(new User(), new Plan(), type);
        PlanResponseDto dto1 = new PlanResponseDto();
        PlanResponseDto dto2 = new PlanResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of(plan1, plan2));
        when(planMapper.toResponseDto(plan1.getPlan())).thenReturn(dto1);
        when(planMapper.toResponseDto(plan2.getPlan())).thenReturn(dto2);

        var result = userPlanService.getAllFromUser(type);

        assertEquals(2, result.size());
        assertTrue(result.contains((BaseUserEntity) dto1));
        assertTrue(result.contains((BaseUserEntity) dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no plans")
    public void getAllFromUser_ShouldReturnEmptyListIfNoPlans() {
        int userId = 1;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userPlanRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userPlanService.getAllFromUser(type);

        assertTrue(result.isEmpty());
        verify(planMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int planId = 100;
        long saveCount = 5;
        long likeCount = 10;
        Plan plan = new Plan();

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(userPlanRepository.countByPlanIdAndType(planId, (short) 1))
                .thenReturn(saveCount);
        when(userPlanRepository.countByPlanIdAndType(planId, (short) 2))
                .thenReturn(likeCount);

        var result = userPlanService.calculateLikesAndSaves(planId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(planRepository).findById(planId);
        verify(userPlanRepository).countByPlanIdAndType(planId, (short) 1);
        verify(userPlanRepository).countByPlanIdAndType(planId, (short) 2);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if plan not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfPlanNotFound() {
        int planId = 100;

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userPlanService.calculateLikesAndSaves(planId));

        verify(userPlanRepository, never()).countByPlanIdAndType(anyInt(), anyShort());
    }
}