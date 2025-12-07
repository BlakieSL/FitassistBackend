package source.code.mapper.workoutSetGroup;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupCreateDto;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupUpdateDto;
import source.code.dto.response.workoutSetGroup.WorkoutSetGroupResponseDto;
import source.code.mapper.workoutSet.WorkoutSetMapper;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSetGroup;
import source.code.repository.WorkoutRepository;
import source.code.service.implementation.helpers.RepositoryHelperImpl;

@Mapper(componentModel = "spring", uses = {WorkoutSetMapper.class})
public abstract class WorkoutSetGroupMapper {
    @Autowired
    private RepositoryHelperImpl repositoryHelper;

    @Autowired
    private WorkoutRepository workoutRepository;

    public abstract WorkoutSetGroupResponseDto toResponseDto(WorkoutSetGroup workoutSetGroup);

    @Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workoutSets", ignore = true)
    public abstract WorkoutSetGroup toEntity(WorkoutSetGroupCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workoutSets", ignore = true)
    @Mapping(target = "workout", source = "workoutId", qualifiedByName = "mapWorkoutIdToWorkout")
    public abstract void updateWorkoutSetGroup(
            @MappingTarget WorkoutSetGroup workoutSetGroup, WorkoutSetGroupUpdateDto updateDto);

    @Named("mapWorkoutIdToWorkout")
    protected Workout mapWorkoutIdToWorkout(int workoutId) {
        return repositoryHelper.find(workoutRepository, Workout.class, workoutId);
    }
}
