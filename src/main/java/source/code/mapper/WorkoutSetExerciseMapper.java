package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseCreateDto;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseUpdateDto;
import source.code.dto.response.workoutSetExercise.WorkoutSetExerciseResponseDto;
import source.code.model.exercise.Exercise;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetExercise;
import source.code.repository.ExerciseRepository;
import source.code.repository.WorkoutSetRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring")
public abstract class WorkoutSetExerciseMapper {

	@Autowired
	private WorkoutSetRepository workoutSetRepository;

	@Autowired
	private ExerciseRepository exerciseRepository;

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Mapping(target = "exerciseId", source = "exercise.id")
	@Mapping(target = "exerciseName", source = "exercise.name")
	public abstract WorkoutSetExerciseResponseDto toResponseDto(WorkoutSetExercise workoutSetExercise);

	@Mapping(target = "workoutSet", source = "workoutSetId", qualifiedByName = "mapWorkoutSetIdToWorkoutSet")
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSetExercise toEntity(WorkoutSetExerciseCreateDto createDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "workoutSet", source = "workoutSetId", qualifiedByName = "mapWorkoutSetIdToWorkoutSet")
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
	@Mapping(target = "id", ignore = true)
	public abstract void updateWorkoutSetExercise(@MappingTarget WorkoutSetExercise workoutSetExercise,
												  WorkoutSetExerciseUpdateDto updateDto);

	@Named("mapWorkoutSetIdToWorkoutSet")
	protected WorkoutSet mapWorkoutSetIdToWorkoutSet(int workoutSetId) {
		return repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId);
	}

	@Named("mapExerciseIdToExercise")
	protected Exercise mapExerciseIdToExercise(int exerciseId) {
		return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
	}

}
