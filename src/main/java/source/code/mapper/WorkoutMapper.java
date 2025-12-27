package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workout.WorkoutCreateDto;
import source.code.dto.request.workout.WorkoutUpdateDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.model.plan.Plan;
import source.code.model.workout.Workout;
import source.code.repository.PlanRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring", uses = { WorkoutSetMapper.class })
public abstract class WorkoutMapper {

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Mapping(target = "weekIndex", ignore = true)
	@Mapping(target = "dayOfWeekIndex", ignore = true)
	public abstract WorkoutResponseDto toResponseDto(Workout workout);

	@Mapping(target = "plan", source = "planId", qualifiedByName = "mapPlanIdToPlan")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	public abstract Workout toEntity(WorkoutCreateDto createDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSets", ignore = true)
	@Mapping(target = "plan", ignore = true)
	public abstract void updateWorkout(@MappingTarget Workout workout, WorkoutUpdateDto updateDto);

	@Named("mapPlanIdToPlan")
	protected Plan mapPlanIdToPlan(int planId) {
		return repositoryHelper.find(planRepository, Plan.class, planId);
	}

}
