package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import source.code.dto.WorkoutSetDto;
import source.code.model.Workout.WorkoutSet;

@Mapper(componentModel = "spring")
public interface WorkoutSetMapper {
  @Mapping(target = "workoutTypeId", source = "workoutType.id")
  @Mapping(target = "exerciseId", source = "exercise.id")
  WorkoutSetDto toDto(WorkoutSet workoutSet);

  @Mapping(target = "id", ignore = true)
  WorkoutSet toEntity(WorkoutSetDto dto);
}
