package source.code.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetNestedCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetNestedUpdateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetExercise;
import source.code.repository.WorkoutRepository;
import source.code.service.implementation.helpers.RepositoryHelperImpl;

@Mapper(componentModel = "spring", uses = { WorkoutSetExerciseMapper.class })
public abstract class WorkoutSetMapper {

	@Autowired
	private RepositoryHelperImpl repositoryHelper;

	@Autowired
	private WorkoutRepository workoutRepository;

	@Autowired
	private WorkoutSetExerciseMapper workoutSetExerciseMapper;

	public abstract WorkoutSetResponseDto toResponseDto(WorkoutSet workoutSet);

	@Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	public abstract WorkoutSet toEntity(WorkoutSetCreateDto createDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	@Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
	public abstract void updateWorkoutSet(@MappingTarget WorkoutSet workoutSet, WorkoutSetUpdateDto updateDto);

	// this is used when creating Workout and need to create new nested WorkoutSet
	@Mapping(target = "workout", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract WorkoutSet toEntityFromNested(WorkoutSetNestedCreateDto createDto);

	@AfterMapping
	protected void setWorkoutSetAssociations(@MappingTarget WorkoutSet workoutSet, WorkoutSetNestedCreateDto dto) {
		workoutSet.getWorkoutSetExercises().forEach(exercise -> exercise.setWorkoutSet(workoutSet));
	}

	// this one is used when updating Workout and need to create new nested WorkoutSet
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
				.map(workoutSetExerciseMapper::toEntityNested)
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
					WorkoutSetExercise newExercise = workoutSetExerciseMapper.toEntityNested(exerciseDto);
					newExercise.setWorkoutSet(workoutSet);
					existingExercises.add(newExercise);
				}
			}
		}
	}

	@Named("mapWorkoutIdToWorkout")
	protected Workout mapWorkoutIdToWorkout(int workoutId) {
		return repositoryHelper.find(workoutRepository, Workout.class, workoutId);
	}

}
