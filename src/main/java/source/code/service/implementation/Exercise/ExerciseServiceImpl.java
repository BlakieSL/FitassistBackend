package source.code.service.implementation.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Exercise.ExerciseCreateEvent;
import source.code.cache.event.Exercise.ExerciseDeleteEvent;
import source.code.cache.event.Exercise.ExerciseUpdateEvent;
import source.code.dto.request.Exercise.ExerciseCreateDto;
import source.code.dto.request.Exercise.ExerciseUpdateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.helper.enumerators.ExerciseField;
import source.code.mapper.Exercise.ExerciseMapper;
import source.code.model.Exercise.*;
import source.code.repository.*;
import source.code.service.declaration.Exercise.ExerciseService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {
  private final ValidationService validationService;
  private final JsonPatchService jsonPatchService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ExerciseMapper exerciseMapper;
  private final RepositoryHelper repositoryHelper;
  private final ExerciseRepository exerciseRepository;
  private final ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository;

  private static final Map<ExerciseField, Function<Exercise, Integer>> fieldExtractorMap = Map.of(
          ExerciseField.EXPERTISE_LEVEL, exercise -> exercise.getExpertiseLevel().getId(),
          ExerciseField.FORCE_TYPE, exercise -> exercise.getForceType().getId(),
          ExerciseField.MECHANICS_TYPE, exercise -> exercise.getMechanicsType().getId(),
          ExerciseField.EQUIPMENT, exercise -> exercise.getExerciseEquipment().getId(),
          ExerciseField.TYPE, exercise -> exercise.getExerciseType().getId());

  public ExerciseServiceImpl(ExerciseMapper exerciseMapper,
                             ValidationService validationService,
                             JsonPatchService jsonPatchService,
                             ApplicationEventPublisher applicationEventPublisher,
                             RepositoryHelper repositoryHelper,
                             ExerciseRepository exerciseRepository,
                             ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository) {
    this.exerciseMapper = exerciseMapper;
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.repositoryHelper = repositoryHelper;
    this.exerciseRepository = exerciseRepository;
    this.exerciseCategoryAssociationRepository = exerciseCategoryAssociationRepository;
  }

  @Transactional
  public ExerciseResponseDto createExercise(ExerciseCreateDto dto) {
    Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(dto));
    applicationEventPublisher.publishEvent(new ExerciseCreateEvent(this, exercise));

    return exerciseMapper.toResponseDto(exercise);
  }

  @Transactional
  public void updateExercise(int exerciseId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    Exercise exercise = find(exerciseId);
    ExerciseUpdateDto patchedExerciseUpdateDto = applyPatchToExercise(exercise, patch);

    validationService.validate(patchedExerciseUpdateDto);

    exerciseMapper.updateExerciseFromDto(exercise, patchedExerciseUpdateDto);
    Exercise savedExercise = exerciseRepository.save(exercise);

    applicationEventPublisher.publishEvent(new ExerciseUpdateEvent(this, savedExercise));
  }

  @Transactional
  public void deleteExercise(int exerciseId) {
    Exercise exercise = find(exerciseId);
    exerciseRepository.delete(exercise);

    applicationEventPublisher.publishEvent(new ExerciseDeleteEvent(this, exercise));
  }


  public List<ExerciseResponseDto> searchExercises(SearchRequestDto dto) {
    return exerciseRepository.findByNameContainingIgnoreCase(dto.getName()).stream()
            .map(exerciseMapper::toResponseDto)
            .toList();
  }

  @Cacheable(value = "exercises", key = "#id")
  public ExerciseResponseDto getExercise(int exerciseId) {
    Exercise exercise = find(exerciseId);
    return exerciseMapper.toResponseDto(exercise);
  }

  @Cacheable(value = "allExercises")
  public List<ExerciseResponseDto> getAllExercises() {
    return repositoryHelper.findAll(exerciseRepository, exerciseMapper::toResponseDto);
  }

  @Cacheable(value = "exercisesByCategory", key = "#categoryId")
  public List<ExerciseResponseDto> getExercisesByCategory(int categoryId) {
    return exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId).stream()
            .map(ExerciseCategoryAssociation::getExercise)
            .map(exerciseMapper::toResponseDto)
            .toList();
  }

  @Cacheable(value = "exercisesByField", key = "#field.name() + '_' + #value")
  public List<ExerciseResponseDto> getExercisesByField(ExerciseField field, int value) {
    Function<Exercise, Integer> fieldExtractor = fieldExtractorMap.get(field);
    if (fieldExtractor == null) {
      throw new IllegalArgumentException("Unknown field: " + field);
    }

    return exerciseRepository.findAll().stream()
            .filter(exercise -> fieldExtractor.apply(exercise).equals(value))
            .map(exerciseMapper::toResponseDto)
            .toList();
  }

  private Exercise find(int exerciseId) {
    return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
  }

  private ExerciseUpdateDto applyPatchToExercise(Exercise exercise, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ExerciseResponseDto responseDto = exerciseMapper.toResponseDto(exercise);
    return jsonPatchService.applyPatch(patch, responseDto, ExerciseUpdateDto.class);
  }
}