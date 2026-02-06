package com.fitassist.backend.mapper.daily;

import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.daily.DailyCartActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class DailyActivityMapper {

	@Mapping(target = "dailyItemId", source = "id")
	@Mapping(target = "id", source = "activity.id")
	@Mapping(target = "name", source = "activity.name")
	@Mapping(target = "met", source = "activity.met")
	@Mapping(target = "category", source = "activity.activityCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "caloriesBurned", ignore = true)
	public abstract ActivityCalculatedResponseDto toResponse(DailyCartActivity dailyCartActivity);

}
