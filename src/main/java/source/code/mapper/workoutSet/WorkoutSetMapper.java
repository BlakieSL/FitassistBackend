package source.code.mapper.workoutSet;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.model.exercise.Exercise;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetGroup;
import source.code.repository.ExerciseRepository;
import source.code.repository.WorkoutSetGroupRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring")
public abstract class WorkoutSetMapper {
    @Autowired
    private WorkoutSetGroupRepository workoutSetGroupRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private RepositoryHelper repositoryHelper;

    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    public abstract WorkoutSetResponseDto toResponseDto(WorkoutSet workoutSet);

    @Mapping(target = "workoutSetGroup", source = "workoutSetGroupId", qualifiedByName = "mapWorkoutSetGroupIdToWorkoutSetGroup")
    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
    @Mapping(target = "id", ignore = true)
    public abstract WorkoutSet toEntity(WorkoutSetCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "workoutSetGroup", source = "workoutSetGroupId", qualifiedByName = "mapWorkoutSetGroupIdToWorkoutSetGroup")
    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
    @Mapping(target = "id", ignore = true)
    public abstract void updateWorkoutSet(@MappingTarget WorkoutSet workoutSet, WorkoutSetUpdateDto updateDto);

    @Named("mapWorkoutSetGroupIdToWorkoutSetGroup")
    protected WorkoutSetGroup mapWorkoutSetGroupIdToWorkoutSetGroup(int workoutSetGroupId) {
        return repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
    }

    @Named("mapExerciseIdToExercise")
    protected Exercise mapExerciseIdToExercise(int exerciseId) {
        return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
    }
}
