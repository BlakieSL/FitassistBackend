package com.fitassist.backend.mapper.plan;

import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedCreateDto;
import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedUpdateDto;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import com.fitassist.backend.dto.response.plan.WorkoutSetResponseDto;
import com.fitassist.backend.model.workout.WorkoutSet;
import com.fitassist.backend.model.workout.WorkoutSetExercise;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { WorkoutSetExerciseMapper.class })
public abstract class WorkoutSetMapper {

	private WorkoutSetExerciseMapper workoutSetExerciseMapper;

	@Autowired
	private void setWorkoutSetExerciseMapper(WorkoutSetExerciseMapper workoutSetExerciseMapper) {
		this.workoutSetExerciseMapper = workoutSetExerciseMapper;
	}

	public abstract WorkoutSetResponseDto toResponseDto(WorkoutSet workoutSet);

	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSet toEntityFromNested(WorkoutSetNestedCreateDto createDto,
			@Context PlanMappingContext context);

	@AfterMapping
	protected void setWorkoutSetAssociations(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedCreateDto dto) {
		workoutSet.getWorkoutSetExercises().forEach(exercise -> exercise.setWorkoutSet(workoutSet));
	}

	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	public abstract WorkoutSet toEntityFromNested(WorkoutSetNestedUpdateDto updateDto,
			@Context PlanMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	public abstract void updateWorkoutSetNested(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedUpdateDto dto,
			@Context PlanMappingContext context);

	@AfterMapping
	protected void setAssociations(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedUpdateDto dto,
			@Context PlanMappingContext context) {
		if (dto.getWorkoutSetExercises() == null) {
			return;
		}

		Set<WorkoutSetExercise> existingExercises = workoutSet.getWorkoutSetExercises();
		boolean isNew = workoutSet.getId() == null;

		if (isNew) {
			List<WorkoutSetExercise> exercises = dto.getWorkoutSetExercises()
				.stream()
				.map(exerciseDto -> workoutSetExerciseMapper.toEntityFromNested(exerciseDto, context))
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
								exerciseDto, context));
				}
				else {
					WorkoutSetExercise newExercise = workoutSetExerciseMapper.toEntityFromNested(exerciseDto, context);
					newExercise.setWorkoutSet(workoutSet);
					existingExercises.add(newExercise);
				}
			}
		}
	}

}
