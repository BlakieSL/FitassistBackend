package source.code.mapper.category;

import org.mapstruct.*;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.request.category.CategoryUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.model.activity.ActivityCategory;

@Mapper(componentModel = "spring")
public abstract class ActivityCategoryMapper implements BaseMapper<ActivityCategory> {

	public abstract CategoryResponseDto toResponseDto(ActivityCategory activityCategory);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activities", ignore = true)
	public abstract ActivityCategory toEntity(CategoryCreateDto request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activities", ignore = true)
	public abstract void updateEntityFromDto(@MappingTarget ActivityCategory activityCategory,
			CategoryUpdateDto request);

}
