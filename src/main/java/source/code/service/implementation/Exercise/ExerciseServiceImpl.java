package source.code.service.implementation.Exercise;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
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
  private final ExerciseRepository exerciseRepository;
  private final UserExerciseRepository userExerciseRepository;
  private final ExerciseCategoryRepository exerciseCategoryRepository;
  private final ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository;

  public ExerciseServiceImpl(ExerciseMapper exerciseMapper,
                             ExerciseRepository exerciseRepository,
                             UserExerciseRepository userExerciseRepository,
                             ExerciseCategoryRepository exerciseCategoryRepository,
                             ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository) {
    this.exerciseMapper = exerciseMapper;
    this.exerciseRepository = exerciseRepository;
    this.userExerciseRepository = userExerciseRepository;
    this.exerciseCategoryRepository = exerciseCategoryRepository;
    this.exerciseCategoryAssociationRepository = exerciseCategoryAssociationRepository;
  }

  @Transactional
  public ExerciseResponseDto createExercise(ExerciseCreateDto dto) {
    Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(dto));
    return exerciseMapper.toDto(exercise);
  }

  public ExerciseResponseDto getExercise(int id) {
    Exercise exercise = exerciseRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Exercise with id: " + id + " not found"));
    return exerciseMapper.toDto(exercise);
  }

  public List<ExerciseResponseDto> getAllExercises() {
    List<Exercise> exercises = exerciseRepository.findAll();
    return exercises.stream()
            .map(exerciseMapper::toDto)
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
            .map(exerciseMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<ExerciseResponseDto> searchExercises(SearchRequestDto dto) {
    List<Exercise> exercises = exerciseRepository.findByNameContainingIgnoreCase(dto.getName());

    return exercises.stream()
            .map(exerciseMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<ExerciseCategoryResponseDto> getAllCategories() {
    List<ExerciseCategory> categories = exerciseCategoryRepository.findAll();

    return categories.stream()
            .map(exerciseMapper::toCategoryDto)
            .collect(Collectors.toList());
  }

  public List<ExerciseResponseDto> getExercisesByCategory(int categoryId) {
    List<ExerciseCategoryAssociation> exerciseCategoryAssociations =
            exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId);

    List<Exercise> exercises = exerciseCategoryAssociations.stream()
            .map(ExerciseCategoryAssociation::getExercise)
            .collect(Collectors.toList());

    return exercises.stream()
            .map(exerciseMapper::toDto)
            .collect(Collectors.toList());
  }

  private <T> List<ExerciseResponseDto> getExercisesByField(Function<Exercise, T> fieldExtractor, Function<T, Integer> idExtractor, int fieldValue) {
    List<Exercise> exercises = exerciseRepository.findAll().stream()
            .filter(exercise -> idExtractor.apply(fieldExtractor.apply(exercise)).equals(fieldValue))
            .collect(Collectors.toList());

    return exercises.stream()
            .map(exerciseMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<ExerciseResponseDto> getExercisesByExpertiseLevel(int expertiseLevelId) {
    return getExercisesByField(Exercise::getExpertiseLevel, ExpertiseLevel::getId, expertiseLevelId);
  }

  public List<ExerciseResponseDto> getExercisesByForceType(int forceTypeId) {
    return getExercisesByField(Exercise::getForceType, ForceType::getId, forceTypeId);
  }

  public List<ExerciseResponseDto> getExercisesByMechanicsType(int mechanicsTypeId) {
    return getExercisesByField(Exercise::getMechanicsType, MechanicsType::getId, mechanicsTypeId);
  }

  public List<ExerciseResponseDto> getExercisesByEquipment(int exerciseEquipmentId) {
    return getExercisesByField(Exercise::getExerciseEquipment, ExerciseEquipment::getId, exerciseEquipmentId);
  }

  public List<ExerciseResponseDto> getExercisesByType(int exerciseTypeId) {
    return getExercisesByField(Exercise::getExerciseType, ExerciseType::getId, exerciseTypeId);
  }
}

