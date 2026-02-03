package com.fitassist.backend.mapper.plan;

import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedCreateDto;
import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedUpdateDto;
import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedUpdateDto;
import com.fitassist.backend.dto.response.plan.WorkoutResponseDto;
import com.fitassist.backend.model.workout.Workout;
import com.fitassist.backend.model.workout.WorkoutSet;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { WorkoutSetMapper.class })
public abstract class WorkoutMapper {

	private WorkoutSetMapper workoutSetMapper;

	@Autowired
	private void setWorkoutSetMapper(WorkoutSetMapper workoutSetMapper) {
		this.workoutSetMapper = workoutSetMapper;
	}

	@Mapping(target = "weekIndex", ignore = true)
	@Mapping(target = "dayOfWeekIndex", ignore = true)
	public abstract WorkoutResponseDto toResponseDto(Workout workout);

	@Mapping(target = "plan", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Workout toEntityFromNested(WorkoutNestedCreateDto createDto, @Context PlanMappingContext context);

	@AfterMapping
	protected void setWorkoutAssociations(@MappingTarget Workout workout, WorkoutNestedCreateDto dto) {
		workout.getWorkoutSets().forEach(workoutSet -> workoutSet.setWorkout(workout));
	}

	@Mapping(target = "plan", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	public abstract Workout toEntityFromNested(WorkoutNestedUpdateDto updateDto, @Context PlanMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "plan", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	public abstract void updateWorkoutNested(@MappingTarget Workout workout, WorkoutNestedUpdateDto dto,
			@Context PlanMappingContext context);

	@AfterMapping
	protected void setWorkoutAssociations(@MappingTarget Workout workout, WorkoutNestedUpdateDto dto,
			@Context PlanMappingContext context) {
		if (dto.getWorkoutSets() == null) {
			return;
		}

		Set<WorkoutSet> existingSets = workout.getWorkoutSets();
		boolean isNew = workout.getId() == null;

		if (isNew) {
			List<WorkoutSet> sets = dto.getWorkoutSets()
				.stream()
				.map(setDto -> workoutSetMapper.toEntityFromNested(setDto, context))
				.peek(set -> set.setWorkout(workout))
				.toList();
			existingSets.addAll(sets);
		}
		else {
			List<Integer> updatedSetIds = dto.getWorkoutSets()
				.stream()
				.map(WorkoutSetNestedUpdateDto::getId)
				.filter(Objects::nonNull)
				.toList();

			existingSets.removeIf(set -> !updatedSetIds.contains(set.getId()));

			for (WorkoutSetNestedUpdateDto setDto : dto.getWorkoutSets()) {
				if (setDto.getId() != null) {
					existingSets.stream()
						.filter(set -> set.getId().equals(setDto.getId()))
						.findFirst()
						.ifPresent(set -> workoutSetMapper.updateWorkoutSetNested(set, setDto, context));
				}
				else {
					WorkoutSet newSet = workoutSetMapper.toEntityFromNested(setDto, context);
					newSet.setWorkout(workout);
					existingSets.add(newSet);
				}
			}
		}
	}

}
