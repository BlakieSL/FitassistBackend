package source.code.mapper.workoutSet;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.model.exercise.Exercise;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.repository.ExerciseRepository;
import source.code.repository.WorkoutRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring")
public abstract class WorkoutSetMapper {
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private RepositoryHelper repositoryHelper;

    @Mapping(target = "workoutId", source = "workout", qualifiedByName = "mapWorkoutToWorkoutId")
    @Mapping(target = "exerciseId", source = "exercise", qualifiedByName = "mapExerciseToExerciseId")
    public abstract WorkoutSetResponseDto toResponseDto(WorkoutSet workoutSet);

    @Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
    @Mapping(target = "id", ignore = true)
    public abstract WorkoutSet toEntity(WorkoutSetCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
    @Mapping(target = "id", ignore = true)
    public abstract void updateWorkoutSet(
            @MappingTarget WorkoutSet workoutSet, WorkoutSetUpdateDto updateDto);

    @Named("mapWorkoutToWorkoutId")
    protected int mapWorkoutToWorkoutId(Workout workout) {
        return workout.getId();
    }

    @Named("mapExerciseToExerciseId")
    protected int mapExerciseToExerciseId(Exercise exercise) {
        return exercise.getId();
    }

    @Named("mapWorkoutIdToWorkout")
    protected Workout mapWorkoutIdToWorkout(int workoutId) {
        return repositoryHelper.find(workoutRepository, Workout.class, workoutId);
    }

    @Named("mapExerciseIdToExercise")
    protected Exercise mapExerciseIdToExercise(int exerciseId) {
        return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
    }
}
