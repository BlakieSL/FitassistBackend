package source.code.service.implementation.Workout;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.response.WorkoutSetResponseDto;
import source.code.mapper.WorkoutSetMapper;
import source.code.model.Exercise.Exercise;
import source.code.model.Workout.WorkoutSet;
import source.code.model.Workout.WorkoutType;
import source.code.repository.ExerciseRepository;
import source.code.repository.WorkoutSetRepository;
import source.code.repository.WorkoutTypeRepository;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Workout.WorkoutSetService;

import java.util.List;

@Service
public class WorkoutSetServiceImpl implements WorkoutSetService {
  private final WorkoutSetMapper workoutSetMapper;
  private final RepositoryHelper repositoryHelper;
  private final WorkoutSetRepository workoutSetRepository;
  private final WorkoutTypeRepository workoutTypeRepository;
  private final ExerciseRepository exerciseRepository;

  public WorkoutSetServiceImpl(WorkoutSetMapper workoutSetMapper,
                               RepositoryHelper repositoryHelper,
                               WorkoutSetRepository workoutSetRepository,
                               WorkoutTypeRepository workoutTypeRepository,
                               ExerciseRepository exerciseRepository) {

    this.workoutSetMapper = workoutSetMapper;
    this.repositoryHelper = repositoryHelper;
    this.workoutSetRepository = workoutSetRepository;
    this.workoutTypeRepository = workoutTypeRepository;
    this.exerciseRepository = exerciseRepository;
  }

  @Transactional
  public WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto request) {
    WorkoutType workoutType = repositoryHelper
            .find(workoutTypeRepository, WorkoutType.class, request.getWorkoutTypeId());

    Exercise exercise = repositoryHelper
            .find(exerciseRepository, Exercise.class, request.getExerciseId());

    WorkoutSet workoutSet = workoutSetMapper.toEntity(request, workoutType, exercise);
    WorkoutSet savedWorkoutSet = workoutSetRepository.save(workoutSet);

    return workoutSetMapper.toResponseDto(savedWorkoutSet);
  }

  @Transactional
  public void deleteWorkoutSet(int id) {
    WorkoutSet workoutSet = find(id);
    workoutSetRepository.delete(workoutSet);
  }

  public WorkoutSetResponseDto getWorkoutSet(int id) {
    WorkoutSet workoutSet = find(id);
    return workoutSetMapper.toResponseDto(workoutSet);
  }

  public List<WorkoutSetResponseDto> getAllWorkoutSets() {
    return repositoryHelper.findAll(workoutSetRepository, workoutSetMapper::toResponseDto);
  }

  public List<WorkoutSetResponseDto> getWorkoutSetsByWorkoutType(int workoutTypeId) {
    return workoutSetRepository.findByWorkoutTypeId(workoutTypeId).stream()
            .map(workoutSetMapper::toResponseDto)
            .toList();
  }

  private WorkoutSet find(int workoutSetId) {
    return repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId);
  }
}