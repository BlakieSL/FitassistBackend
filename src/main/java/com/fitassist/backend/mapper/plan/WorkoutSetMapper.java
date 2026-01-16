package com.fitassist.backend.mapper.plan;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedCreateDto;
import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedUpdateDto;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import com.fitassist.backend.dto.response.plan.WorkoutSetResponseDto;
import com.fitassist.backend.model.workout.WorkoutSet;
import com.fitassist.backend.model.workout.WorkoutSetExercise;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { WorkoutSetExerciseMapper.class })
public abstract class WorkoutSetMapper {

	@Autowired
	private WorkoutSetExerciseMapper workoutSetExerciseMapper;

	public abstract WorkoutSetResponseDto toResponseDto(WorkoutSet workoutSet);

	// this is used when creating Workout and need to create new nested WorkoutSet
	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSet toEntityFromNested(WorkoutSetNestedCreateDto createDto);

	@AfterMapping
	protected void setWorkoutSetAssociations(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedCreateDto dto) {
		workoutSet.getWorkoutSetExercises().forEach(exercise -> exercise.setWorkoutSet(workoutSet));
	}

	// this is used when updating Workout and need to create new nested WorkoutSet
	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	public abstract WorkoutSet toEntityFromNested(WorkoutSetNestedUpdateDto updateDto);

	// this is used when updating Workout and need to update existing nested WorkoutSet
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	public abstract void updateWorkoutSetNested(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedUpdateDto dto);

	@AfterMapping
	protected void setAssociations(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedUpdateDto dto) {
		if (dto.getWorkoutSetExercises() == null) {
			return;
		}

		Set<WorkoutSetExercise> existingExercises = workoutSet.getWorkoutSetExercises();
		boolean isNew = workoutSet.getId() == null;

		if (isNew) {
			List<WorkoutSetExercise> exercises = dto.getWorkoutSetExercises()
				.stream()
				.map(workoutSetExerciseMapper::toEntityFromNested)
				.peek(exercise -> exercise.setWorkoutSet(workoutSet))
				.toList();
			existingExercises.addAll(exercises);
		}
		else {
			List<Integer> updatedExerciseIds = dto.getWorkoutSetExercises()
				.stream()
				.map(WorkoutSetExerciseNestedUpdateDto::getId)
				.filter(Objects::nonNull)
				.toList();

			existingExercises.removeIf(exercise -> !updatedExerciseIds.contains(exercise.getId()));

			for (WorkoutSetExerciseNestedUpdateDto exerciseDto : dto.getWorkoutSetExercises()) {
				if (exerciseDto.getId() != null) {
					existingExercises.stream()
						.filter(exercise -> exercise.getId().equals(exerciseDto.getId()))
						.findFirst()
						.ifPresent(exercise -> workoutSetExerciseMapper.updateWorkoutSetExerciseNested(exercise,
								exerciseDto));
				}
				else {
					WorkoutSetExercise newExercise = workoutSetExerciseMapper.toEntityFromNested(exerciseDto);
					newExercise.setWorkoutSet(workoutSet);
					existingExercises.add(newExercise);
				}
			}
		}
	}

}
