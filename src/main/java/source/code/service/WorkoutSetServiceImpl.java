package source.code.service;

import source.code.dto.WorkoutSetDto;
import source.code.helper.ValidationHelper;
import source.code.mapper.WorkoutSetMapper;
import source.code.model.WorkoutSet;
import source.code.model.WorkoutType;
import source.code.model.Exercise;
import source.code.repository.WorkoutSetRepository;
import source.code.repository.WorkoutTypeRepository;
import source.code.repository.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.service.interfaces.WorkoutSetService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkoutSetServiceImpl implements WorkoutSetService {
    private final ValidationHelper validationHelper;
    private final WorkoutSetMapper workoutSetMapper;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutSetServiceImpl(ValidationHelper validationHelper,
                                 WorkoutSetMapper workoutSetMapper,
                                 WorkoutSetRepository workoutSetRepository,
                                 WorkoutTypeRepository workoutTypeRepository,
                                 ExerciseRepository exerciseRepository) {
        this.validationHelper = validationHelper;
        this.workoutSetMapper = workoutSetMapper;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutTypeRepository = workoutTypeRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public WorkoutSetDto createWorkoutSet(WorkoutSetDto workoutSetDto) {
        validationHelper.validate(workoutSetDto);

        WorkoutType workoutType = workoutTypeRepository
                .findById(workoutSetDto.getWorkoutTypeId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout type with id: " +
                                workoutSetDto.getWorkoutTypeId() + " not found"));

        Exercise exercise = exerciseRepository
                .findById(workoutSetDto.getExerciseId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " +
                                workoutSetDto.getExerciseId() + " not found"));

        WorkoutSet workoutSet = workoutSetMapper.toEntity(workoutSetDto);
        workoutSet.setWorkoutType(workoutType);
        workoutSet.setExercise(exercise);

        WorkoutSet savedWorkoutSet = workoutSetRepository.save(workoutSet);
        return workoutSetMapper.toDto(savedWorkoutSet);
    }

    @Transactional
    public void deleteWorkoutSet(int id) {
        WorkoutSet workoutSet = workoutSetRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "WorkoutSet with id: " + id + " not found"));
        workoutSetRepository.delete(workoutSet);
    }

    public WorkoutSetDto getWorkoutSet(int id) {
        WorkoutSet workoutSet = workoutSetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "WorkoutSet with id: " + id + " not found"));
        return workoutSetMapper.toDto(workoutSet);
    }

    public List<WorkoutSetDto> getAllWorkoutSets() {
        List<WorkoutSet> workoutSets = workoutSetRepository.findAll();
        return workoutSets.stream()
                .map(workoutSetMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<WorkoutSetDto> getWorkoutSetsByWorkoutType(int workoutTypeId) {
        List<WorkoutSet> workoutSets = workoutSetRepository
                .findByWorkoutTypeId(workoutTypeId);
        return workoutSets.stream()
                .map(workoutSetMapper::toDto)
                .collect(Collectors.toList());
    }
}