package source.code.mapper;

import source.code.dto.WorkoutSetDto;
import source.code.model.WorkoutSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutSetMapper {
    @Mapping(target = "workoutTypeId", source = "workoutType.id")
    @Mapping(target = "exerciseId", source = "exercise.id")
    WorkoutSetDto toDto(WorkoutSet workoutSet);

    @Mapping(target = "id", ignore = true)
    WorkoutSet toEntity(WorkoutSetDto dto);
}
