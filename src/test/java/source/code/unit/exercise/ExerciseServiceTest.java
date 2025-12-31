package source.code.unit.exercise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.exercise.ExerciseCreateDto;
import source.code.dto.request.exercise.ExerciseUpdateDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.event.events.Exercise.ExerciseCreateEvent;
import source.code.event.events.Exercise.ExerciseDeleteEvent;
import source.code.event.events.Exercise.ExerciseUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.ExerciseMapper;
import source.code.mapper.PlanMapper;
import source.code.model.exercise.Exercise;
import source.code.model.plan.Plan;
import source.code.repository.*;
import source.code.service.declaration.exercise.ExercisePopulationService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.implementation.exercise.ExerciseServiceImpl;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

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
	void createExercise_shouldCreateExerciseAndPublish() {
		exercise.setId(exerciseId);
		when(exerciseMapper.toEntity(createDto)).thenReturn(exercise);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId))
			.thenReturn(Optional.of(exercise));
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(exerciseMapper.toResponseDto(exercise)).thenReturn(responseDto);

		ExerciseResponseDto result = exerciseService.createExercise(createDto);

		assertEquals(responseDto, result);
		verify(exerciseRepository).flush();
		verify(exercisePopulationService).populate(responseDto);
	}

	@Test
	void createExercise_shouldPublishEvent() {
		ArgumentCaptor<ExerciseCreateEvent> eventCaptor = ArgumentCaptor.forClass(ExerciseCreateEvent.class);

		exercise.setId(exerciseId);
		when(exerciseMapper.toEntity(createDto)).thenReturn(exercise);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId))
			.thenReturn(Optional.of(exercise));
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(java.util.Optional.of(exercise));
		when(exerciseMapper.toResponseDto(exercise)).thenReturn(responseDto);

		exerciseService.createExercise(createDto);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(exercise, eventCaptor.getValue().getExercise());
	}

	@Test
	void updateExercise_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		exercise.setId(exerciseId);
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class)).thenReturn(patchedDto);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId))
			.thenReturn(Optional.of(exercise));

		exerciseService.updateExercise(exerciseId, patch);

		verify(validationService).validate(patchedDto);
		verify(exerciseMapper).updateExerciseFromDto(exercise, patchedDto);
		verify(exerciseRepository).save(exercise);
	}

	@Test
	void updateExercise_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
		ArgumentCaptor<ExerciseUpdateEvent> eventCaptor = ArgumentCaptor.forClass(ExerciseUpdateEvent.class);

		exercise.setId(exerciseId);
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(Optional.of(exercise));
		when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class)).thenReturn(patchedDto);
		when(exerciseRepository.save(exercise)).thenReturn(exercise);
		when(exerciseRepository.findByIdWithAssociationsForIndexing(exerciseId))
			.thenReturn(Optional.of(exercise));

		exerciseService.updateExercise(exerciseId, patch);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
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

		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(java.util.Optional.of(exercise));
		when(exerciseMapper.toResponseDto(exercise)).thenReturn(responseDto);
		when(planRepository.findByExerciseIdWithDetails(exerciseId)).thenReturn(List.of(plan));
		when(planMapper.toSummaryDto(plan)).thenReturn(planSummaryDto);

		ExerciseResponseDto result = exerciseService.getExercise(exerciseId);

		assertEquals(responseDto, result);
		verify(exerciseRepository).findByIdWithDetails(exerciseId);
		verify(exerciseMapper).toResponseDto(exercise);
		verify(exercisePopulationService).populate(responseDto);
		verify(planRepository).findByExerciseIdWithDetails(exerciseId);
		verify(planMapper).toSummaryDto(plan);
		verify(planPopulationService).populate(any(List.class));
	}

	@Test
	void getExercise_shouldThrowExceptionWhenExerciseNotFound() {
		when(exerciseRepository.findByIdWithDetails(exerciseId)).thenReturn(java.util.Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> exerciseService.getExercise(exerciseId));

		verify(exerciseRepository).findByIdWithDetails(exerciseId);
		verifyNoInteractions(exerciseMapper);
	}

	@Test
	void getFilteredExercises_shouldReturnFilteredExercises() {
		Page<Exercise> exercisePage = new PageImpl<>(List.of(exercise), pageable, 1);

		when(exerciseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(exercisePage);
		when(exerciseMapper.toSummaryDto(exercise)).thenReturn(summaryDto);

		Page<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter, pageable);

		assertEquals(1, result.getTotalElements());
		assertSame(summaryDto, result.getContent().get(0));
		verify(exerciseRepository).findAll(any(Specification.class), eq(pageable));
		verify(exerciseMapper).toSummaryDto(exercise);
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
