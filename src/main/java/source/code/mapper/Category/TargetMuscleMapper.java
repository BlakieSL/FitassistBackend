package source.code.mapper.Category;

import org.mapstruct.*;
import source.code.dto.Request.Category.CategoryCreateDto;
import source.code.dto.Request.Category.CategoryUpdateDto;
import source.code.dto.Response.Category.CategoryResponseDto;
import source.code.model.Exercise.TargetMuscle;

@Mapper(componentModel = "spring")
public abstract class TargetMuscleMapper implements BaseMapper<TargetMuscle> {

  public abstract CategoryResponseDto toResponseDto(TargetMuscle targetMuscle);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseTargetMuscles", ignore = true)
  public abstract TargetMuscle toEntity(CategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseTargetMuscles", ignore = true)
  public abstract void updateEntityFromDto(@MappingTarget TargetMuscle category,
                                           CategoryUpdateDto request);
}
