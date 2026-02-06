package com.fitassist.backend.unit.exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.exercise.ExerciseUpdateDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.event.event.Exercise.ExerciseCreateEvent;
import com.fitassist.backend.event.event.Exercise.ExerciseDeleteEvent;
import com.fitassist.backend.event.event.Exercise.ExerciseUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.exercise.ExerciseMapper;
import com.fitassist.backend.mapper.exercise.ExerciseMappingContext;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.model.exercise.*;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.repository.*;
import com.fitassist.backend.service.declaration.exercise.ExercisePopulationService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.implementation.exercise.ExerciseServiceImpl;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
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
public class ExerciseServiceTest {

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private ExerciseMapper exerciseMapper;

	@Mock
	private PlanMapper planMapper;

	@Mock
	private ValidationService validationService;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private ExerciseRepository exerciseRepository;

	@Mock
	private ExerciseTargetMuscleRepository exerciseTargetMuscleRepository;

	@Mock
	private PlanRepository planRepository;

	@Mock
	private EquipmentRepository equipmentRepository;

	@Mock
	private ExpertiseLevelRepository expertiseLevelRepository;

	@Mock
	private ForceTypeRepository forceTypeRepository;

	@Mock
	private MechanicsTypeRepository mechanicsTypeRepository;

	@Mock
	private TargetMuscleRepository targetMuscleRepository;

	@Mock
	private ExercisePopulationService exercisePopulationService;

	@Mock
	private PlanPopulationService planPopulationService;

	@Mock
	private SpecificationDependencies dependencies;

	@InjectMocks
	private ExerciseServiceImpl exerciseService;

	private Exercise exercise;

	private ExerciseCreateDto createDto;

	private ExerciseResponseDto responseDto;

	private ExerciseSummaryDto summaryDto;

	private JsonMergePatch patch;

	private ExerciseUpdateDto patchedDto;

	private int exerciseId;

	private int expertiseLevelId;

	private int mechanicsTypeId;

	private int forceTypeId;

	private int equipmentId;

	private ExpertiseLevel expertiseLevel;

	private MechanicsType mechanicsType;

	private ForceType forceType;

	private Equipment equipment;

	private FilterDto filter;

	private Pageable pageable;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		exercise = new Exercise();
		createDto = new ExerciseCreateDto();
		responseDto = new ExerciseResponseDto();
		summaryDto = new ExerciseSummaryDto();
		patchedDto = new ExerciseUpdateDto();
		exerciseId = 1;
		expertiseLevelId = 1;
		mechanicsTypeId = 2;
		forceTypeId = 3;
		equipmentId = 4;
		expertiseLevel = new ExpertiseLevel();
		mechanicsType = new MechanicsType();
		forceType = new ForceType();
		equipment = new Equipment();
		filter = new FilterDto();
		pageable = PageRequest.of(0, 100);
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);

		createDto.setExpertiseLevelId(expertiseLevelId);
		createDto.setMechanicsTypeId(mechanicsTypeId);
		createDto.setForceTypeId(forceTypeId);
		createDto.setEquipmentId(equipmentId);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createExercise_shouldCreateExerciseAndPublish() {
		exercise.setId(exerciseId);
		when(expertiseLevelRepository.findById(expertiseLevelId)).thenReturn(Optional.of(expertiseLevel));
		when(mechanicsTypeRepository.findById(mechanicsTypeId)).thenReturn(Optional.of(mechanicsType));
		when(forceTypeRepository.findById(forceTypeId)).thenReturn(Optional.of(forceType));
		when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
		when(exerciseMapper.toEntity(eq(createDto), any(ExerciseMappingContext.class))).thenReturn(exercise);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId)).thenReturn(Optional.of(exercise));
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(exerciseMapper.toResponse(exercise)).thenReturn(responseDto);

		ExerciseResponseDto result = exerciseService.createExercise(createDto);

		assertEquals(responseDto, result);
		verify(exerciseRepository).flush();
		verify(exercisePopulationService).populate(responseDto);
	}

	@Test
	void createExercise_shouldPublishEvent() {
		ArgumentCaptor<ExerciseCreateEvent> eventCaptor = ArgumentCaptor.forClass(ExerciseCreateEvent.class);

		exercise.setId(exerciseId);
		when(expertiseLevelRepository.findById(expertiseLevelId)).thenReturn(Optional.of(expertiseLevel));
		when(mechanicsTypeRepository.findById(mechanicsTypeId)).thenReturn(Optional.of(mechanicsType));
		when(forceTypeRepository.findById(forceTypeId)).thenReturn(Optional.of(forceType));
		when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
		when(exerciseMapper.toEntity(eq(createDto), any(ExerciseMappingContext.class))).thenReturn(exercise);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId)).thenReturn(Optional.of(exercise));
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(exerciseMapper.toResponse(exercise)).thenReturn(responseDto);

		exerciseService.createExercise(createDto);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(exercise, eventCaptor.getValue().getExercise());
	}

	@Test
	void createExercise_shouldThrowExceptionWhenExpertiseLevelNotFound() {
		when(expertiseLevelRepository.findById(expertiseLevelId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> exerciseService.createExercise(createDto));

		verify(exerciseRepository, never()).save(any());
	}

	@Test
	void updateExercise_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		exercise.setId(exerciseId);
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class)).thenReturn(patchedDto);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId)).thenReturn(Optional.of(exercise));

		exerciseService.updateExercise(exerciseId, patch);

		verify(validationService).validate(patchedDto);
		verify(exerciseMapper).update(eq(exercise), eq(patchedDto), any(ExerciseMappingContext.class));
		verify(exerciseRepository).save(exercise);
	}

	@Test
	void updateExercise_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
		ArgumentCaptor<ExerciseUpdateEvent> eventCaptor = ArgumentCaptor.forClass(ExerciseUpdateEvent.class);

		exercise.setId(exerciseId);
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class)).thenReturn(patchedDto);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId)).thenReturn(Optional.of(exercise));

		exerciseService.updateExercise(exerciseId, patch);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		verify(exerciseMapper).update(eq(exercise), eq(patchedDto), any(ExerciseMappingContext.class));
		assertEquals(exercise, eventCaptor.getValue().getExercise());
	}

	@Test
	void updateExercise_shouldThrowExceptionWhenExerciseNotFound() {
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> exerciseService.updateExercise(exerciseId, patch));

		verifyNoInteractions(exerciseMapper, jsonPatchService, validationService, eventPublisher);
		verify(exerciseRepository, never()).save(exercise);
	}

	@Test
	void updateExercise_shouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class)).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> exerciseService.updateExercise(exerciseId, patch));

		verifyNoInteractions(validationService, eventPublisher);
		verify(exerciseRepository, never()).save(exercise);
	}

	@Test
	void updateExercise_shouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class)).thenReturn(patchedDto);

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> exerciseService.updateExercise(exerciseId, patch));

		verify(validationService).validate(patchedDto);
		verifyNoInteractions(eventPublisher);
		verify(exerciseRepository, never()).save(exercise);
	}

	@Test
	void deleteExercise_shouldDelete() {
		when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId)).thenReturn(exercise);

		exerciseService.deleteExercise(exerciseId);

		verify(exerciseRepository).delete(exercise);
	}

	@Test
	void deleteExercise_shouldPublishEvent() {
		ArgumentCaptor<ExerciseDeleteEvent> eventCaptor = ArgumentCaptor.forClass(ExerciseDeleteEvent.class);

		when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId)).thenReturn(exercise);

		exerciseService.deleteExercise(exerciseId);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(exercise, eventCaptor.getValue().getExercise());
	}

	@Test
	void deleteExercise_shouldThrowExceptionWhenExerciseNotFound() {
		when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
			.thenThrow(RecordNotFoundException.of(Exercise.class, exerciseId));

		assertThrows(RecordNotFoundException.class, () -> exerciseService.deleteExercise(exerciseId));

		verify(exerciseRepository, never()).delete(exercise);
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	void getExercise_shouldReturnExerciseWhenFound() {
		Plan plan = new Plan();
		plan.setId(1);
		PlanSummaryDto planSummaryDto = new PlanSummaryDto();
		planSummaryDto.setId(1);

		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(exerciseMapper.toResponse(exercise)).thenReturn(responseDto);
		when(planRepository.findByExerciseIdWithDetails(exerciseId)).thenReturn(List.of(plan));
		when(planMapper.toSummary(plan)).thenReturn(planSummaryDto);

		ExerciseResponseDto result = exerciseService.getExercise(exerciseId);

		assertEquals(responseDto, result);
		verify(exerciseRepository).findByIdWithDetails(exerciseId);
		verify(exerciseMapper).toResponse(exercise);
		verify(exercisePopulationService).populate(responseDto);
		verify(planRepository).findByExerciseIdWithDetails(exerciseId);
		verify(planMapper).toSummary(plan);
		verify(planPopulationService).populate(any(List.class));
	}

	@Test
	void getExercise_shouldThrowExceptionWhenExerciseNotFound() {
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> exerciseService.getExercise(exerciseId));

		verify(exerciseRepository).findByIdWithDetails(exerciseId);
		verifyNoInteractions(exerciseMapper);
	}

	@Test
	void getFilteredExercises_shouldReturnFilteredExercises() {
		Page<Exercise> exercisePage = new PageImpl<>(List.of(exercise), pageable, 1);

		when(exerciseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(exercisePage);
		when(exerciseMapper.toSummary(exercise)).thenReturn(summaryDto);

		Page<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter, pageable);

		assertEquals(1, result.getTotalElements());
		assertSame(summaryDto, result.getContent().get(0));
		verify(exerciseRepository).findAll(any(Specification.class), eq(pageable));
		verify(exerciseMapper).toSummary(exercise);
	}

	@Test
	void getFilteredExercises_shouldReturnEmptyPageWhenFilterHasNoCriteria() {
		filter.setFilterCriteria(new ArrayList<>());
		Page<Exercise> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

		when(exerciseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter, pageable);

		assertTrue(result.isEmpty());
		verify(exerciseRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void getFilteredExercises_shouldReturnEmptyPageWhenNoExercisesMatchFilter() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("nonexistentKey");
		filter.setFilterCriteria(List.of(criteria));
		Page<Exercise> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

		when(exerciseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter, pageable);

		assertTrue(result.isEmpty());
		verify(exerciseRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void getAllExerciseEntities_shouldReturnAllExerciseEntities() {
		List<Exercise> exercises = List.of(exercise);
		when(exerciseRepository.findAll()).thenReturn(exercises);

		List<Exercise> result = exerciseService.getAllExerciseEntities();

		assertEquals(exercises, result);
		verify(exerciseRepository).findAll();
	}

	@Test
	void getAllExerciseEntities_shouldReturnEmptyListWhenNoExercises() {
		List<Exercise> exercises = List.of();
		when(exerciseRepository.findAll()).thenReturn(exercises);

		List<Exercise> result = exerciseService.getAllExerciseEntities();

		assertTrue(result.isEmpty());
		verify(exerciseRepository).findAll();
	}

}
