package com.fitassist.backend.mapper.plan;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedCreateDto;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import com.fitassist.backend.dto.response.plan.WorkoutSetExerciseResponseDto;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.workout.WorkoutSetExercise;
import com.fitassist.backend.repository.ExerciseRepository;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring")
public abstract class WorkoutSetExerciseMapper {

	@Autowired
	private ExerciseRepository exerciseRepository;

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Mapping(target = "exerciseId", source = "exercise.id")
	@Mapping(target = "exerciseName", source = "exercise.name")
	public abstract WorkoutSetExerciseResponseDto toResponseDto(WorkoutSetExercise workoutSetExercise);

	// this is used when creating WorkoutSet and need to create new nested
	// WorkoutSetExercise
	@Mapping(target = "workoutSet", ignore = true)
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSetExercise toEntityFromNested(WorkoutSetExerciseNestedCreateDto createDto);

	// this is used when updating WorkoutSet and need to create new nested
	// WorkoutSetExercise
	@Mapping(target = "workoutSet", ignore = true)
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSetExercise toEntityFromNested(WorkoutSetExerciseNestedUpdateDto updateDto);

	// this is used when updating WorkoutSet and need to update existing nested
	// WorkoutSetExercise
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSet", ignore = true)
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExerciseIdToExercise")
	public abstract void updateWorkoutSetExerciseNested(@MappingTarget WorkoutSetExercise exercise,
			WorkoutSetExerciseNestedUpdateDto dto);

	@Named("mapExerciseIdToExercise")
	protected Exercise mapExerciseIdToExercise(int exerciseId) {
		return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
	}

}
