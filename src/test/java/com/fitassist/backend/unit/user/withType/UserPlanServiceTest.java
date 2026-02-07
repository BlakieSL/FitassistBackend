package com.fitassist.backend.unit.user.withType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotSupportedInteractionTypeException;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import com.fitassist.backend.model.user.interactions.UserPlan;
import com.fitassist.backend.repository.PlanRepository;
import com.fitassist.backend.repository.UserPlanRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withType.UserPlanServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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
	private PlanPopulationService planPopulationService;

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
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(false);
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
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

		assertThrows(NotSupportedInteractionTypeException.class, () -> userPlanService.saveToUser(planId, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		int userId = 1;
		int planId = 100;
		TypeOfInteraction type = TypeOfInteraction.SAVE;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userPlanService.saveToUser(planId, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		int userId = 1;
		int planId = 100;
		TypeOfInteraction type = TypeOfInteraction.SAVE;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userPlanService.saveToUser(planId, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfPlanNotFound() {
		int userId = 1;
		int planId = 100;
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		User user = new User();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(planRepository.findById(planId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userPlanService.saveToUser(planId, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		int userId = 1;
		int planId = 100;
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		UserPlan userPlan = UserPlan.of(new User(), new Plan(), type);

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userPlanRepository.findByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(Optional.of(userPlan));

		userPlanService.deleteFromUser(planId, type);

		verify(userPlanRepository).delete(userPlan);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserPlanNotFound() {
		int userId = 1;
		int planId = 100;
		TypeOfInteraction type = TypeOfInteraction.SAVE;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userPlanRepository.findByUserIdAndPlanIdAndType(userId, planId, type)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userPlanService.deleteFromUser(planId, type));

		verify(userPlanRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllPlansByType() {
		int userId = 1;
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Plan plan1 = new Plan();
		plan1.setId(1);
		Plan plan2 = new Plan();
		plan2.setId(2);

		User user = new User();
		user.setId(userId);

		UserPlan userPlan1 = UserPlan.of(user, plan1, type);
		UserPlan userPlan2 = UserPlan.of(user, plan2, type);

		PlanSummaryDto dto1 = new PlanSummaryDto();
		dto1.setId(1);
		PlanSummaryDto dto2 = new PlanSummaryDto();
		dto2.setId(2);

		Page<UserPlan> userPlanPage = new PageImpl<>(List.of(userPlan1, userPlan2), pageable, 2);

		when(userPlanRepository.findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class)))
			.thenReturn(userPlanPage);
		when(planRepository.findByIdsWithDetails(any())).thenReturn(List.of(plan1, plan2));
		when(planMapper.toSummary(plan1)).thenReturn(dto1);
		when(planMapper.toSummary(plan2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userPlanService.getAllFromUser(userId, type, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(userPlanRepository).findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class));
		verify(planPopulationService).populate(any(List.class));
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoPlans() {
		int userId = 1;
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<UserPlan> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userPlanRepository.findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class)))
			.thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userPlanService.getAllFromUser(userId, type, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
