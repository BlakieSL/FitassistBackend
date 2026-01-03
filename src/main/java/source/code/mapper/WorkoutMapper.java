package source.code.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workout.WorkoutCreateDto;
import source.code.dto.request.workout.WorkoutNestedCreateDto;
import source.code.dto.request.workout.WorkoutNestedUpdateDto;
import source.code.dto.request.workout.WorkoutUpdateDto;
import source.code.dto.request.workoutSet.WorkoutSetNestedUpdateDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.model.plan.Plan;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.repository.PlanRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring", uses = { WorkoutSetMapper.class })
public abstract class WorkoutMapper {

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Autowired
	private WorkoutSetMapper workoutSetMapper;

	@Mapping(target = "weekIndex", ignore = true)
	@Mapping(target = "dayOfWeekIndex", ignore = true)
	public abstract WorkoutResponseDto toResponseDto(Workout workout);

	@Mapping(target = "plan", source = "planId", qualifiedByName = "mapPlanIdToPlan")
	@Mapping(target = "id", ignore = true)
	public abstract Workout toEntity(WorkoutCreateDto createDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	@Mapping(target = "plan", ignore = true)
	public abstract void updateWorkout(@MappingTarget Workout workout, WorkoutUpdateDto updateDto);

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

	@Named("mapPlanIdToPlan")
	protected Plan mapPlanIdToPlan(int planId) {
		return repositoryHelper.find(planRepository, Plan.class, planId);
	}

}
