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

	private static final int USER_ID = 1;

	private static final int PLAN_ID = 100;

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

	private User user;

	private Plan plan;

	private UserPlan userPlan;

	@BeforeEach
	void setUp() {
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		plan = new Plan();
		plan.setId(PLAN_ID);
		plan.setIsPublic(true);
		userPlan = UserPlan.of(user, plan, TypeOfInteraction.SAVE);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(planRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));

		userPlanService.saveToUser(PLAN_ID, type);

		verify(userPlanRepository).save(any(UserPlan.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotSupportedInteractionTypeExceptionIfPlanIsPrivate() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		plan.setIsPublic(false);

		when(userPlanRepository.existsByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(planRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));

		assertThrows(NotSupportedInteractionTypeException.class, () -> userPlanService.saveToUser(PLAN_ID, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userPlanService.saveToUser(PLAN_ID, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userPlanService.saveToUser(PLAN_ID, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfPlanNotFound() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userPlanRepository.existsByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(planRepository.findById(PLAN_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userPlanService.saveToUser(PLAN_ID, type));

		verify(userPlanRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userPlanRepository.findByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(Optional.of(userPlan));

		userPlanService.deleteFromUser(PLAN_ID, type);

		verify(userPlanRepository).delete(userPlan);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserPlanNotFound() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userPlanRepository.findByUserIdAndPlanIdAndType(USER_ID, PLAN_ID, type)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userPlanService.deleteFromUser(PLAN_ID, type));

		verify(userPlanRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllPlansByType() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Plan plan2 = new Plan();
		plan2.setId(2);
		UserPlan userPlan2 = UserPlan.of(user, plan2, type);

		PlanSummaryDto dto1 = new PlanSummaryDto();
		dto1.setId(PLAN_ID);
		PlanSummaryDto dto2 = new PlanSummaryDto();
		dto2.setId(2);

		Page<UserPlan> userPlanPage = new PageImpl<>(List.of(userPlan, userPlan2), pageable, 2);

		when(userPlanRepository.findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class)))
			.thenReturn(userPlanPage);
		when(planRepository.findByIdsWithDetails(any())).thenReturn(List.of(plan, plan2));
		when(planMapper.toSummary(plan)).thenReturn(dto1);
		when(planMapper.toSummary(plan2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userPlanService.getAllFromUser(USER_ID, type, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(userPlanRepository).findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class));
		verify(planPopulationService).populate(any(List.class));
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoPlans() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<UserPlan> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userPlanRepository.findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class)))
			.thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userPlanService.getAllFromUser(USER_ID, type, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
