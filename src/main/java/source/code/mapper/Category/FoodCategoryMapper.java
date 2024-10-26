package source.code.mapper.Category;

import org.mapstruct.*;
import source.code.dto.Request.Category.CategoryCreateDto;
import source.code.dto.Request.Category.CategoryUpdateDto;
import source.code.dto.Response.CategoryResponseDto;
import source.code.model.Food.FoodCategory;

@Mapper(componentModel = "spring")
public abstract class FoodCategoryMapper implements BaseMapper<FoodCategory> {
  public abstract CategoryResponseDto toResponseDto(FoodCategory category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "foods", ignore = true)
  public abstract FoodCategory toEntity(CategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "foods", ignore = true)
  public abstract void updateEntityFromDto(@MappingTarget FoodCategory category,
                                           CategoryUpdateDto request);
}
