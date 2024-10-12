package source.code.mapper;

        import org.mapstruct.*;
        import source.code.dto.WorkoutDto;
        import source.code.model.Workout.Workout;
        import source.code.model.Workout.WorkoutType;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
  @Mapping(target = "workoutTypeId", source = "workoutType.id")
  WorkoutDto toDto(Workout workout);

  @Mapping(target = "id", ignore = true)
  Workout toEntity(WorkoutDto dto, @Context WorkoutType workoutType);

  @AfterMapping
  default void mapWorkoutType(@MappingTarget Workout workout, @Context WorkoutType workoutType) {
    workout.setWorkoutType(workoutType);
  }
}
