package source.code.mapper;

import org.mapstruct.*;
import source.code.dto.WorkoutSetDto;
import source.code.model.Exercise.Exercise;
import source.code.model.Workout.WorkoutSet;
import source.code.model.Workout.WorkoutType;

@Mapper(componentModel = "spring")
public interface WorkoutSetMapper {
  @Mapping(target = "workoutTypeId", source = "workoutType.id")
  @Mapping(target = "exerciseId", source = "exercise.id")
  WorkoutSetDto toDto(WorkoutSet workoutSet);

  @Mapping(target = "id", ignore = true)
  WorkoutSet toEntity(WorkoutSetDto dto, @Context WorkoutType workoutType, @Context Exercise exercise);

  @AfterMapping
  default void setWorkoutType(@MappingTarget WorkoutSet workoutSet, @Context WorkoutType workoutType) {
    workoutSet.setWorkoutType(workoutType);
  }


  @AfterMapping
  default void setExercise(@MappingTarget WorkoutSet workoutSet, @Context Exercise exercise) {
    workoutSet.setExercise(exercise);
  }

}
