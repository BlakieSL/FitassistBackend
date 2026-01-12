package source.code.mapper.daily;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.model.activity.ActivityCategory;
import source.code.model.daily.DailyCartActivity;
import source.code.service.declaration.helpers.CalculationsService;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

		responseDto.setCaloriesBurned(caloriesBurned.setScale(0, RoundingMode.HALF_UP).intValue());
	}

	@Named("mapActivityCategoryToResponseDto")
	protected CategoryResponseDto mapActivityCategoryToResponseDto(ActivityCategory category) {
		return new CategoryResponseDto(category.getId(), category.getName());
	}

}
