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
import source.code.service.declaration.Helpers.ValidationService;
import source.code.helper.enumerators.ExerciseField;
import source.code.mapper.Exercise.ExerciseMapper;
import source.code.model.Exercise.*;
import source.code.repository.*;
import source.code.service.declaration.Exercise.ExerciseService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {
  private final ExerciseMapper exerciseMapper;
  private final ValidationService validationService;
  private final JsonPatchService jsonPatchService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ExerciseRepository exerciseRepository;
  private final UserExerciseRepository userExerciseRepository;
  private final ExerciseCategoryRepository exerciseCategoryRepository;
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
                             ExerciseRepository exerciseRepository,
                             UserExerciseRepository userExerciseRepository,
                             ExerciseCategoryRepository exerciseCategoryRepository,
                             ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository) {
    this.exerciseMapper = exerciseMapper;
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.exerciseRepository = exerciseRepository;
    this.userExerciseRepository = userExerciseRepository;
    this.exerciseCategoryRepository = exerciseCategoryRepository;
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

    Exercise exercise = getExerciseOrThrow(exerciseId);
    ExerciseUpdateDto patchedExerciseUpdateDto = applyPatchToExercise(exercise, patch);

    validationService.validate(patchedExerciseUpdateDto);

    exerciseMapper.updateExerciseFromDto(exercise, patchedExerciseUpdateDto);
    Exercise savedExercise = exerciseRepository.save(exercise);

    applicationEventPublisher.publishEvent(new ExerciseUpdateEvent(this, savedExercise));
  }

  @Transactional
  public void deleteExercise(int exerciseId) {
    Exercise exercise = getExerciseOrThrow(exerciseId);
    exerciseRepository.delete(exercise);

    applicationEventPublisher.publishEvent(new ExerciseDeleteEvent(this, exercise));
  }


  public List<ExerciseResponseDto> searchExercises(SearchRequestDto dto) {
    List<Exercise> exercises = exerciseRepository.findByNameContainingIgnoreCase(dto.getName());

    return exercises.stream()
            .map(exerciseMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "exercises", key = "#id")
  public ExerciseResponseDto getExercise(int id) {
    Exercise exercise = getExerciseOrThrow(id);
    return exerciseMapper.toResponseDto(exercise);
  }

  @Cacheable(value = "allExercises")
  public List<ExerciseResponseDto> getAllExercises() {
    List<Exercise> exercises = exerciseRepository.findAll();
    return exercises.stream()
            .map(exerciseMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "exercisesByCategory", key = "#categoryId")
  public List<ExerciseResponseDto> getExercisesByCategory(int categoryId) {
    List<ExerciseCategoryAssociation> exerciseCategoryAssociations =
            exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId);

    List<Exercise> exercises = exerciseCategoryAssociations.stream()
            .map(ExerciseCategoryAssociation::getExercise)
            .collect(Collectors.toList());

    return exercises.stream()
            .map(exerciseMapper::toResponseDto)
            .collect(Collectors.toList());
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
            .collect(Collectors.toList());
  }

  private Exercise getExerciseOrThrow(int exerciseId) {
    return exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Exercise with id: " + exerciseId + " not found"));
  }

  private ExerciseUpdateDto applyPatchToExercise(Exercise exercise, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ExerciseResponseDto responseDto = exerciseMapper.toResponseDto(exercise);
    return jsonPatchService.applyPatch(patch, responseDto, ExerciseUpdateDto.class);
  }
}