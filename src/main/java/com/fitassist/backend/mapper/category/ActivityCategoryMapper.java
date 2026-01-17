package com.fitassist.backend.mapper.category;

import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.activity.ActivityCategory;
import org.mapstruct.*;

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
