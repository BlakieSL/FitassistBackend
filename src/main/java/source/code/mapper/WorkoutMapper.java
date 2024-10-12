package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import source.code.dto.WorkoutDto;
import source.code.model.Workout.Workout;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
  @Mapping(target = "workoutTypeId", source = "workoutType.id")
  WorkoutDto toDto(Workout workout);

  @Mapping(target = "id", ignore = true)
  Workout toEntity(WorkoutDto dto);
}
