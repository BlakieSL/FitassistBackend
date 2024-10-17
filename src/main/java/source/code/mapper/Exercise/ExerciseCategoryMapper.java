package source.code.mapper.Exercise;

import org.mapstruct.*;
import source.code.dto.request.ExerciseCategoryCreateDto;
import source.code.dto.request.ExerciseCategoryUpdateDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.model.Exercise.ExerciseCategory;

@Mapper(componentModel = "spring")
public abstract class ExerciseCategoryMapper {

  public abstract ExerciseCategoryResponseDto toResponseDto(ExerciseCategory category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseCategoryAssociations", ignore = true)
  public abstract ExerciseCategory toEntity(ExerciseCategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseCategoryAssociations", ignore = true)
  public abstract void updateExercise(@MappingTarget ExerciseCategory category,
                                      ExerciseCategoryUpdateDto request);
}
