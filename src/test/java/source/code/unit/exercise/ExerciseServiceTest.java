package source.code.unit.exercise;

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
import org.springframework.context.ApplicationEventPublisher;
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
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.mapper.plan.PlanMapper;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;
import source.code.model.plan.Plan;
import source.code.repository.ExerciseRepository;
import source.code.repository.ExerciseTargetMuscleRepository;
import source.code.repository.PlanRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.implementation.exercise.ExerciseServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    private PlanPopulationService planPopulationService;
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
        when(exerciseMapper.toEntity(createDto)).thenReturn(exercise);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseMapper.toSummaryDto(exercise)).thenReturn(summaryDto);

        ExerciseSummaryDto result = exerciseService.createExercise(createDto);

        assertEquals(summaryDto, result);
    }

    @Test
    void createExercise_shouldPublishEvent() {
        ArgumentCaptor<ExerciseCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(ExerciseCreateEvent.class);

        when(exerciseMapper.toEntity(createDto)).thenReturn(exercise);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseMapper.toSummaryDto(exercise)).thenReturn(summaryDto);

        exerciseService.createExercise(createDto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(exercise, eventCaptor.getValue().getExercise());
    }

    @Test
    void updateExercise_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenReturn(exercise);
        when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class))
                .thenReturn(patchedDto);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        exerciseService.updateExercise(exerciseId, patch);

        verify(validationService).validate(patchedDto);
        verify(exerciseMapper).updateExerciseFromDto(exercise, patchedDto);
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void updateExercise_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
        ArgumentCaptor<ExerciseUpdateEvent> eventCaptor = ArgumentCaptor
                .forClass(ExerciseUpdateEvent.class);

        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenReturn(exercise);
        when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class))
                .thenReturn(patchedDto);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        exerciseService.updateExercise(exerciseId, patch);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(exercise, eventCaptor.getValue().getExercise());
    }

    @Test
    void updateExercise_shouldThrowExceptionWhenExerciseNotFound() {
        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenThrow(RecordNotFoundException.of(Exercise.class, exerciseId));

        assertThrows(RecordNotFoundException.class,
                () -> exerciseService.updateExercise(exerciseId, patch)
        );

        verifyNoInteractions(exerciseMapper, jsonPatchService, validationService, eventPublisher);
        verify(exerciseRepository, never()).save(exercise);
    }

    @Test
    void updateExercise_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenReturn(exercise);
        when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class,
                () -> exerciseService.updateExercise(exerciseId, patch)
        );

        verifyNoInteractions(validationService, eventPublisher);
        verify(exerciseRepository, never()).save(exercise);
    }

    @Test
    void updateExercise_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenReturn(exercise);
        when(jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class,
                () -> exerciseService.updateExercise(exerciseId, patch)
        );

        verify(validationService).validate(patchedDto);
        verifyNoInteractions(eventPublisher);
        verify(exerciseRepository, never()).save(exercise);
    }

    @Test
    void deleteExercise_shouldDelete() {
        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenReturn(exercise);

        exerciseService.deleteExercise(exerciseId);

        verify(exerciseRepository).delete(exercise);
    }

    @Test
    void deleteExercise_shouldPublishEvent() {
        ArgumentCaptor<ExerciseDeleteEvent> eventCaptor = ArgumentCaptor
                .forClass(ExerciseDeleteEvent.class);

        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenReturn(exercise);

        exerciseService.deleteExercise(exerciseId);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(exercise, eventCaptor.getValue().getExercise());
    }

    @Test
    void deleteExercise_shouldThrowExceptionWhenExerciseNotFound() {
        when(repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId))
                .thenThrow(RecordNotFoundException.of(Exercise.class, exerciseId));

        assertThrows(RecordNotFoundException.class,
                () -> exerciseService.deleteExercise(exerciseId)
        );

        verify(exerciseRepository, never()).delete(exercise);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void getExercise_shouldReturnExerciseWhenFound() {
        Plan plan = new Plan();
        plan.setId(1);
        PlanSummaryDto planSummaryDto = new PlanSummaryDto();
        planSummaryDto.setId(1);

        when(exerciseRepository.findByIdWithMedia(exerciseId))
                .thenReturn(java.util.Optional.of(exercise));
        when(exerciseMapper.toDetailedResponseDto(exercise)).thenReturn(responseDto);
        when(planRepository.findByExerciseIdWithDetails(exerciseId)).thenReturn(List.of(plan));
        when(planMapper.toSummaryDto(plan)).thenReturn(planSummaryDto);

        ExerciseResponseDto result = exerciseService.getExercise(exerciseId);

        assertEquals(responseDto, result);
        verify(exerciseRepository).findByIdWithMedia(exerciseId);
        verify(exerciseMapper).toDetailedResponseDto(exercise);
        verify(planRepository).findByExerciseIdWithDetails(exerciseId);
        verify(planMapper).toSummaryDto(plan);
        verify(planPopulationService).populate(any(List.class));
    }

    @Test
    void getExercise_shouldThrowExceptionWhenExerciseNotFound() {
        when(exerciseRepository.findByIdWithMedia(exerciseId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> exerciseService.getExercise(exerciseId));

        verify(exerciseRepository).findByIdWithMedia(exerciseId);
        verifyNoInteractions(exerciseMapper);
    }

    @Test
    void getAllExercises_shouldReturnAllExercises() {
        List<ExerciseSummaryDto> responseDtos = List.of(summaryDto);

        when(repositoryHelper.findAll(eq(exerciseRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<ExerciseSummaryDto> result = exerciseService.getAllExercises();

        assertEquals(responseDtos, result);
        verify(repositoryHelper).findAll(eq(exerciseRepository), any(Function.class));
    }

    @Test
    void getAllExercises_shouldReturnEmptyListWhenNoExercises() {
        List<ExerciseSummaryDto> responseDtos = List.of();
        when(repositoryHelper.findAll(eq(exerciseRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<ExerciseSummaryDto> result = exerciseService.getAllExercises();

        assertTrue(result.isEmpty());
        verify(repositoryHelper).findAll(eq(exerciseRepository), any(Function.class));
    }

    @Test
    void getFilteredExercises_shouldReturnFilteredExercises() {
        when(exerciseRepository.findAll(any(Specification.class))).thenReturn(List.of(exercise));
        when(exerciseMapper.toSummaryDto(exercise)).thenReturn(summaryDto);

        List<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter);

        assertEquals(1, result.size());
        assertSame(summaryDto, result.get(0));
        verify(exerciseRepository).findAll(any(Specification.class));
        verify(exerciseMapper).toSummaryDto(exercise);
    }

    @Test
    void getFilteredExercises_shouldReturnEmptyListWhenFilterHasNoCriteria() {
        filter.setFilterCriteria(new ArrayList<>());

        when(exerciseRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter);

        assertTrue(result.isEmpty());
        verify(exerciseRepository).findAll(any(Specification.class));
        verifyNoInteractions(exerciseMapper);
    }

    @Test
    void getFilteredExercises_shouldReturnEmptyListWhenNoExercisesMatchFilter() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setFilterKey("nonexistentKey");
        filter.setFilterCriteria(List.of(criteria));

        when(exerciseRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<ExerciseSummaryDto> result = exerciseService.getFilteredExercises(filter);

        assertTrue(result.isEmpty());
        verify(exerciseRepository).findAll(any(Specification.class));
        verifyNoInteractions(exerciseMapper);
    }

    @Test
    void getAllExerciseEntities_shouldReturnAllExerciseEntities() {
        List<Exercise> exercises = List.of(exercise);
        when(exerciseRepository.findAllWithoutAssociations()).thenReturn(exercises);

        List<Exercise> result = exerciseService.getAllExerciseEntities();

        assertEquals(exercises, result);
        verify(exerciseRepository).findAllWithoutAssociations();
    }

    @Test
    void getAllExerciseEntities_shouldReturnEmptyListWhenNoExercises() {
        List<Exercise> exercises = List.of();
        when(exerciseRepository.findAllWithoutAssociations()).thenReturn(exercises);

        List<Exercise> result = exerciseService.getAllExerciseEntities();

        assertTrue(result.isEmpty());
        verify(exerciseRepository).findAllWithoutAssociations();
    }

    @Test
    void getExercisesByCategory_shouldReturnExercisesForCategory() {
        int categoryId = 1;
        ExerciseTargetMuscle exerciseTargetMuscle = new ExerciseTargetMuscle();
        exerciseTargetMuscle.setExercise(exercise);

        when(exerciseTargetMuscleRepository.findByTargetMuscleId(categoryId))
                .thenReturn(List.of(exerciseTargetMuscle));
        when(exerciseMapper.toSummaryDto(exercise)).thenReturn(summaryDto);

        List<ExerciseSummaryDto> result = exerciseService.getExercisesByCategory(categoryId);

        assertEquals(1, result.size());
        assertSame(summaryDto, result.get(0));
        verify(exerciseTargetMuscleRepository).findByTargetMuscleId(categoryId);
        verify(exerciseMapper).toSummaryDto(exercise);
    }

    @Test
    void getExercisesByCategory_shouldReturnEmptyListWhenNoExercises() {
        int categoryId = 1;
        when(exerciseTargetMuscleRepository.findByTargetMuscleId(categoryId))
                .thenReturn(new ArrayList<>());

        List<ExerciseSummaryDto> result = exerciseService.getExercisesByCategory(categoryId);

        assertTrue(result.isEmpty());
        verify(exerciseTargetMuscleRepository).findByTargetMuscleId(categoryId);
        verifyNoInteractions(exerciseMapper);
    }
}
