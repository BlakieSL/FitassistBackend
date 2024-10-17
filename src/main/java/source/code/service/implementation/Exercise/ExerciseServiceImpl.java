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
import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.ExerciseUpdateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.helper.enumerators.ExerciseField;
import source.code.mapper.ExerciseMapper;
import source.code.model.Exercise.*;
import source.code.model.User.UserExercise;
import source.code.repository.*;
import source.code.service.declaration.ExerciseService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {
  private final ExerciseMapper exerciseMapper;
  private final ValidationHelper validationHelper;
  private final JsonPatchHelper jsonPatchHelper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ExerciseRepository exerciseRepository;
  private final UserExerciseRepository userExerciseRepository;
  private final ExerciseCategoryRepository exerciseCategoryRepository;
  private final ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository;

  public ExerciseServiceImpl(ExerciseMapper exerciseMapper,
                             ValidationHelper validationHelper,
                             JsonPatchHelper jsonPatchHelper,
                             ApplicationEventPublisher applicationEventPublisher,
                             ExerciseRepository exerciseRepository,
                             UserExerciseRepository userExerciseRepository,
                             ExerciseCategoryRepository exerciseCategoryRepository,
                             ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository) {
    this.exerciseMapper = exerciseMapper;
    this.validationHelper = validationHelper;
    this.jsonPatchHelper = jsonPatchHelper;
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
    ExerciseUpdateDto patchedExerciseUpdateDto = applyPatchToExercise(exerciseId, patch);

    validationHelper.validate(patchedExerciseUpdateDto);

    exerciseMapper.updateExerciseFromDto(exercise, patchedExerciseUpdateDto);
    Exercise savedExercise = exerciseRepository.save(exercise);

    applicationEventPublisher.publishEvent(new ExerciseUpdateEvent(this, exercise));
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

  public List<ExerciseResponseDto> getExercisesByUserAndType(int userId, short type) {
    List<UserExercise> userExercises = userExerciseRepository.findByUserIdAndType(userId, type);

    List<Exercise> exercises = userExercises
            .stream()
            .map(UserExercise::getExercise)
            .collect(Collectors.toList());

    return exercises
            .stream()
            .map(exerciseMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "allExerciseCategories")
  public List<ExerciseCategoryResponseDto> getAllCategories() {
    List<ExerciseCategory> categories = exerciseCategoryRepository.findAll();

    return categories.stream()
            .map(exerciseMapper::toCategoryDto)
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
    switch (field) {
      case EXPERTISE_LEVEL:
        return getExercisesByField(Exercise::getExpertiseLevel, ExpertiseLevel::getId, value);
      case FORCE_TYPE:
        return getExercisesByField(Exercise::getForceType, ForceType::getId, value);
      case MECHANICS_TYPE:
        return getExercisesByField(Exercise::getMechanicsType, MechanicsType::getId, value);
      case EQUIPMENT:
        return getExercisesByField(Exercise::getExerciseEquipment, ExerciseEquipment::getId, value);
      case TYPE:
        return getExercisesByField(Exercise::getExerciseType, ExerciseType::getId, value);
      default:
        throw new IllegalArgumentException("Unknown field: " + field);
    }
  }

  private <T> List<ExerciseResponseDto> getExercisesByField(Function<Exercise, T> fieldExtractor,
                                                            Function<T, Integer> idExtractor,
                                                            int fieldValue) {
    List<Exercise> exercises = exerciseRepository.findAll().stream()
            .filter(exercise -> idExtractor.apply(fieldExtractor.apply(exercise)).equals(fieldValue))
            .collect(Collectors.toList());

    return exercises.stream()
            .map(exerciseMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  private Exercise getExerciseOrThrow(int exerciseId) {
    return exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Exercise with id: " + exerciseId + " not found"));
  }

  private ExerciseUpdateDto applyPatchToExercise(int exerciseId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    Exercise exercise = getExerciseOrThrow(exerciseId);
    ExerciseResponseDto responseDto = exerciseMapper.toResponseDto(exercise);
    return jsonPatchHelper.applyPatch(patch, responseDto, ExerciseUpdateDto.class);
  }
}