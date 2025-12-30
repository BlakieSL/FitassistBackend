package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetNestedCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.repository.WorkoutRepository;
import source.code.service.implementation.helpers.RepositoryHelperImpl;

@Mapper(componentModel = "spring", uses = { WorkoutSetExerciseMapper.class })
public abstract class WorkoutSetMapper {

	@Autowired
	private RepositoryHelperImpl repositoryHelper;

	@Autowired
	private WorkoutRepository workoutRepository;

	public abstract WorkoutSetResponseDto toResponseDto(WorkoutSet workoutSet);

	@Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	public abstract WorkoutSet toEntity(WorkoutSetCreateDto createDto);

	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSet toEntityNested(WorkoutSetNestedCreateDto createDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	@Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
	public abstract void updateWorkoutSet(@MappingTarget WorkoutSet workoutSet, WorkoutSetUpdateDto updateDto);

	@AfterMapping
	protected void setWorkoutSetAssociations(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedCreateDto dto) {
		workoutSet.getWorkoutSetExercises().forEach(exercise -> exercise.setWorkoutSet(workoutSet));
	}

	@Named("mapWorkoutIdToWorkout")
	protected Workout mapWorkoutIdToWorkout(int workoutId) {
		return repositoryHelper.find(workoutRepository, Workout.class, workoutId);
	}

}
