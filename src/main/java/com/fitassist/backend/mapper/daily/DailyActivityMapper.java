package com.fitassist.backend.mapper.daily;

import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.activity.ActivityCategory;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public abstract class DailyActivityMapper {

	@Autowired
	private CalculationsService calculationsService;

	@Mapping(target = "dailyItemId", source = "id")
	@Mapping(target = "id", source = "activity.id")
	@Mapping(target = "name", source = "activity.name")
	@Mapping(target = "met", source = "activity.met")
	@Mapping(target = "category", source = "activity.activityCategory",
			qualifiedByName = "mapActivityCategoryToResponseDto")
	@Mapping(target = "caloriesBurned", ignore = true)
	public abstract ActivityCalculatedResponseDto toActivityCalculatedResponseDto(DailyCartActivity dailyCartActivity);

	@AfterMapping
	protected void setCaloriesBurned(@MappingTarget ActivityCalculatedResponseDto responseDto,
			DailyCartActivity dailyCartActivity) {
		BigDecimal caloriesBurned = calculationsService.calculateCaloriesBurned(dailyCartActivity.getTime(),
				dailyCartActivity.getWeight(), dailyCartActivity.getActivity().getMet());

		responseDto.setCaloriesBurned(caloriesBurned);
	}

	@Named("mapActivityCategoryToResponseDto")
	protected CategoryResponseDto mapActivityCategoryToResponseDto(ActivityCategory category) {
		return new CategoryResponseDto(category.getId(), category.getName());
	}

}
