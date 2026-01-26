package com.fitassist.backend.unit.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.request.plan.PlanUpdateDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.event.event.Plan.PlanCreateEvent;
import com.fitassist.backend.event.event.Plan.PlanDeleteEvent;
import com.fitassist.backend.event.event.Plan.PlanUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.repository.PlanCategoryAssociationRepository;
import com.fitassist.backend.repository.PlanRepository;
import com.fitassist.backend.repository.TextRepository;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.implementation.plan.PlanServiceImpl;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

	@Mock
	private PlanMapper planMapper;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private PlanRepository planRepository;

	@Mock
	private PlanCategoryAssociationRepository planCategoryAssociationRepository;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private ValidationService validationService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private TextRepository textRepository;

	@Mock
	private PlanPopulationService planPopulationService;

	@InjectMocks
	private PlanServiceImpl planService;

	private Plan plan;

	private PlanCreateDto createDto;

	private PlanResponseDto responseDto;

	private PlanSummaryDto summaryDto;

	private JsonMergePatch patch;

	private PlanUpdateDto patchedDto;

	private int planId;

	private int userId;

	private FilterDto filter;

	private Pageable pageable;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		plan = new Plan();
		createDto = new PlanCreateDto();
		responseDto = new PlanResponseDto();
		summaryDto = new PlanSummaryDto();
		patchedDto = new PlanUpdateDto();
		planId = 1;
		userId = 1;
		filter = new FilterDto();
		pageable = PageRequest.of(0, 100);
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createPlan_shouldCreatePlan() {
		plan.setId(planId);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(planMapper.toEntity(createDto, userId)).thenReturn(plan);
		when(planRepository.save(plan)).thenReturn(plan);
		when(planRepository.findByIdWithDetails(planId)).thenReturn(Optional.of(plan));
		when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

		PlanResponseDto result = planService.createPlan(createDto);

		assertEquals(responseDto, result);
		verify(planPopulationService).populate(responseDto);
	}

	@Test
	void createPlan_shouldPublish() {
		plan.setId(planId);
		ArgumentCaptor<PlanCreateEvent> eventCaptor = ArgumentCaptor.forClass(PlanCreateEvent.class);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(planMapper.toEntity(createDto, userId)).thenReturn(plan);
		when(planRepository.save(plan)).thenReturn(plan);
		when(planRepository.findByIdWithDetails(planId)).thenReturn(Optional.of(plan));
		when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

		planService.createPlan(createDto);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(plan, eventCaptor.getValue().getPlan());
	}

	@Test
	void updatePlan_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class)).thenReturn(patchedDto);
		when(planRepository.save(plan)).thenReturn(plan);

		planService.updatePlan(planId, patch);

		verify(validationService).validate(patchedDto);
		verify(planMapper).updatePlan(plan, patchedDto);
		verify(planRepository).save(plan);
	}

	@Test
	void updatePlan_shouldPublish() throws JsonPatchException, JsonProcessingException {
		ArgumentCaptor<PlanUpdateEvent> eventCaptor = ArgumentCaptor.forClass(PlanUpdateEvent.class);
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class)).thenReturn(patchedDto);
		when(planRepository.save(plan)).thenReturn(plan);

		planService.updatePlan(planId, patch);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(plan, eventCaptor.getValue().getPlan());
	}

	@Test
	void updatePlan_shouldThrowExceptionWhenPlanNotFound() {
		when(repositoryHelper.find(planRepository, Plan.class, planId))
			.thenThrow(RecordNotFoundException.of(Plan.class, planId));

		assertThrows(RecordNotFoundException.class, () -> planService.updatePlan(planId, patch));

		verifyNoInteractions(planMapper, jsonPatchService, validationService, eventPublisher);
		verify(planRepository, never()).save(plan);
	}

	@Test
	void updatePlan_shouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class)).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> planService.updatePlan(planId, patch));

		verifyNoInteractions(validationService, eventPublisher);
		verify(planRepository, never()).save(plan);
	}

	@Test
	void updatePlan_shouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class)).thenReturn(patchedDto);

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> planService.updatePlan(planId, patch));

		verify(validationService).validate(patchedDto);
		verifyNoInteractions(eventPublisher);
		verify(planRepository, never()).save(plan);
	}

	@Test
	void deletePlan_shouldDelete() {
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);

		planService.deletePlan(planId);

		verify(planRepository).delete(plan);
		verify(eventPublisher).publishEvent(any(PlanDeleteEvent.class));
	}

	@Test
	void deletePlan_shouldThrowExceptionWhenPlanNotFound() {
		when(repositoryHelper.find(planRepository, Plan.class, planId))
			.thenThrow(RecordNotFoundException.of(Plan.class, planId));

		assertThrows(RecordNotFoundException.class, () -> planService.deletePlan(planId));

		verify(planRepository, never()).delete(plan);
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	void getPlan_shouldReturnPlanWhenFound() {
		when(planRepository.findByIdWithDetails(planId)).thenReturn(Optional.of(plan));
		when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

		PlanResponseDto result = planService.getPlan(planId);

		assertEquals(responseDto, result);
		verify(planPopulationService).populate(responseDto);
	}

	@Test
	void getPlan_shouldThrowExceptionWhenPlanNotFound() {
		when(planRepository.findByIdWithDetails(planId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> planService.getPlan(planId));

		verifyNoInteractions(planMapper);
	}

	@Test
	void getFilteredPlans_shouldReturnFilteredPlans() {
		Page<Plan> planPage = new PageImpl<>(List.of(plan), pageable, 1);

		when(planRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(planPage);
		when(planMapper.toSummaryDto(plan)).thenReturn(summaryDto);

		Page<PlanSummaryDto> result = planService.getFilteredPlans(filter, pageable);

		assertEquals(1, result.getTotalElements());
		assertSame(summaryDto, result.getContent().get(0));
		verify(planRepository).findAll(any(Specification.class), eq(pageable));
		verify(planMapper).toSummaryDto(plan);
		verify(planPopulationService).populate(anyList());
	}

	@Test
	void getFilteredPlans_shouldReturnEmptyPageWhenFilterHasNoCriteria() {
		filter.setFilterCriteria(new ArrayList<>());
		Page<Plan> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

		when(planRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<PlanSummaryDto> result = planService.getFilteredPlans(filter, pageable);

		assertTrue(result.isEmpty());
		verify(planRepository).findAll(any(Specification.class), eq(pageable));
		verify(planPopulationService).populate(anyList());
	}

	@Test
	void getFilteredPlans_shouldReturnEmptyPageWhenNoPlansMatchFilter() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("nonexistentKey");
		filter.setFilterCriteria(List.of(criteria));
		Page<Plan> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

		when(planRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<PlanSummaryDto> result = planService.getFilteredPlans(filter, pageable);

		assertTrue(result.isEmpty());
		verify(planRepository).findAll(any(Specification.class), eq(pageable));
		verify(planPopulationService).populate(anyList());
	}

	@Test
	void incrementViews_shouldCallRepositoryIncrementViews() {
		planService.incrementViews(planId);

		verify(planRepository).incrementViews(planId);
	}

	@Test
	void incrementViews_shouldCallRepositoryWithCorrectId() {
		int specificPlanId = 42;

		planService.incrementViews(specificPlanId);

		verify(planRepository).incrementViews(specificPlanId);
		verify(planRepository, never()).incrementViews(planId);
	}

}
