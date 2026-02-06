package com.fitassist.backend.mapper.plan;

import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedCreateDto;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import com.fitassist.backend.dto.response.plan.WorkoutSetExerciseResponseDto;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.workout.WorkoutSetExercise;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class WorkoutSetExerciseMapper {

	@Mapping(target = "exerciseId", source = "exercise.id")
	@Mapping(target = "exerciseName", source = "exercise.name")
	public abstract WorkoutSetExerciseResponseDto toResponse(WorkoutSetExercise workoutSetExercise);

	@Mapping(target = "workoutSet", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExercise")
	public abstract WorkoutSetExercise toEntity(WorkoutSetExerciseNestedCreateDto dto,
			@Context PlanMappingContext context);

	@Mapping(target = "workoutSet", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExercise")
	public abstract WorkoutSetExercise toEntity(WorkoutSetExerciseNestedUpdateDto dto,
			@Context PlanMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSet", ignore = true)
	@Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "mapExercise")
	public abstract void update(@MappingTarget WorkoutSetExercise exercise, WorkoutSetExerciseNestedUpdateDto dto,
			@Context PlanMappingContext context);

	@Named("mapExercise")
	protected Exercise mapExercise(Integer exerciseId, @Context PlanMappingContext context) {
		return context.getExercise(exerciseId);
	}

}
