package source.code.mapper.Activity;

import org.mapstruct.*;
import source.code.dto.request.ActivityCategoryCreateDto;
import source.code.dto.request.ActivityCategoryUpdateDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.model.Activity.ActivityCategory;

@Mapper(componentModel = "spring")
public abstract class ActivityCategoryMapper {
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  public abstract ActivityCategoryResponseDto toResponseDto(ActivityCategory activityCategory);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "activities", ignore = true)
  public abstract ActivityCategory toEntity(ActivityCategoryCreateDto request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "activities", ignore = true)
  public abstract void updateCategory(@MappingTarget ActivityCategory activityCategory,
                                      ActivityCategoryUpdateDto request);
}
