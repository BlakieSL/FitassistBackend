package source.code.mapper.plan;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.plan.workout.WorkoutNestedCreateDto;
import source.code.dto.request.plan.workout.WorkoutNestedUpdateDto;
import source.code.dto.request.plan.workoutSet.WorkoutSetNestedUpdateDto;
import source.code.dto.response.plan.WorkoutResponseDto;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { WorkoutSetMapper.class })
public abstract class WorkoutMapper {

	@Autowired
	private WorkoutSetMapper workoutSetMapper;

	@Mapping(target = "weekIndex", ignore = true)
	@Mapping(target = "dayOfWeekIndex", ignore = true)
	public abstract WorkoutResponseDto toResponseDto(Workout workout);

	// this is used when creating Plan and need to create new nested Workout
	@Mapping(target = "plan", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Workout toEntityFromNested(WorkoutNestedCreateDto createDto);

	@AfterMapping
	protected void setWorkoutAssociations(@MappingTarget Workout workout, WorkoutNestedCreateDto dto) {
		workout.getWorkoutSets().forEach(workoutSet -> workoutSet.setWorkout(workout));
	}

	// this is used when updating Plan and need to create new nested Workout
	@Mapping(target = "plan", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	public abstract Workout toEntityFromNested(WorkoutNestedUpdateDto updateDto);

	// this is used when updating Plan and need to update existing nested Workout
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "plan", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	public abstract void updateWorkoutNested(@MappingTarget Workout workout, WorkoutNestedUpdateDto dto);

	@AfterMapping
	protected void setWorkoutAssociations(@MappingTarget Workout workout, WorkoutNestedUpdateDto dto) {
		if (dto.getWorkoutSets() == null) {
			return;
		}

		Set<WorkoutSet> existingSets = workout.getWorkoutSets();
		boolean isNew = workout.getId() == null;

		if (isNew) {
			List<WorkoutSet> sets = dto.getWorkoutSets()
				.stream()
				.map(workoutSetMapper::toEntityFromNested)
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
						.ifPresent(set -> workoutSetMapper.updateWorkoutSetNested(set, setDto));
				}
				else {
					WorkoutSet newSet = workoutSetMapper.toEntityFromNested(setDto);
					newSet.setWorkout(workout);
					existingSets.add(newSet);
				}
			}
		}
	}

}
