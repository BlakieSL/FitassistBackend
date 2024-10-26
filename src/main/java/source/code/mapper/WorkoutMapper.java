package source.code.mapper;

import org.mapstruct.*;
import source.code.dto.Request.Workout.WorkoutCreateDto;
import source.code.dto.Response.WorkoutResponseDto;
import source.code.model.Workout.Workout;
import source.code.model.Workout.WorkoutType;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
  @Mapping(target = "workoutTypeId", source = "workoutType.id")
  WorkoutResponseDto toResponseDto(Workout workout);

  @Mapping(target = "id", ignore = true)
  Workout toEntity(WorkoutCreateDto request, @Context WorkoutType workoutType);

  @AfterMapping
  default void mapWorkoutType(@MappingTarget Workout workout, @Context WorkoutType workoutType) {
    workout.setWorkoutType(workoutType);
  }
}
