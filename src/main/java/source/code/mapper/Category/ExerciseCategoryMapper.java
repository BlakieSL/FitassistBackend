package source.code.mapper.Category;

import org.mapstruct.*;
import source.code.dto.Request.Category.CategoryCreateDto;
import source.code.dto.Request.Category.CategoryUpdateDto;
import source.code.dto.Response.CategoryResponseDto;
import source.code.model.Exercise.ExerciseCategory;

@Mapper(componentModel = "spring")
public abstract class ExerciseCategoryMapper implements BaseMapper<ExerciseCategory> {

  public abstract CategoryResponseDto toResponseDto(ExerciseCategory category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseCategoryAssociations", ignore = true)
  public abstract ExerciseCategory toEntity(CategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseCategoryAssociations", ignore = true)
  public abstract void updateEntityFromDto(@MappingTarget ExerciseCategory category,
                                           CategoryUpdateDto request);
}
