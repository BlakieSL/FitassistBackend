package source.code.mapper.Plan;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.mapper.Generics.BaseMapper;
import source.code.model.Plan.PlanCategory;

public abstract class PlanCategoryMapper implements BaseMapper<PlanCategory> {
  public abstract CategoryResponseDto toResponseDto(PlanCategory category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "planCategoryAssociations", ignore = true)
  public abstract PlanCategory toEntity(CategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "planCategoryAssociations", ignore = true)
  public abstract void updateEntityFromDto(@MappingTarget PlanCategory category, CategoryUpdateDto request);
}
