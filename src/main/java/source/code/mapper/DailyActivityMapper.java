package source.code.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.helper.CalculationsHelper;
import source.code.model.DailyActivityItem;

@Mapper(componentModel = "spring")
public abstract class DailyActivityMapper {
  @Autowired
  private CalculationsHelper calculationsHelper;

  @Mapping(target = "id", source = "dailyActivityItem.activity.id")
  @Mapping(target = "name", source = "dailyActivityItem.activity.name")
  @Mapping(target = "met", source = "dailyActivityItem.activity.met")
  @Mapping(target = "categoryName", source = "dailyActivityItem.activity.activityCategory.name")
  @Mapping(target = "categoryId", source = "dailyActivityItem.activity.activityCategory.id")
  @Mapping(target = "caloriesBurned", ignore = true)
  @Mapping(target = "time", source = "dailyActivityItem.time")
  public abstract ActivityCalculatedResponseDto toActivityCalculatedResponseDto(DailyActivityItem dailyActivityItem, double userWeight);

  @AfterMapping
  protected void setCaloriesBurned(
          @MappingTarget ActivityCalculatedResponseDto responseDto,
          DailyActivityItem dailyActivityItem,
          double userWeight) {

    double calories = calculationsHelper.calculateCaloriesBurned(
            dailyActivityItem.getTime(),
            userWeight,
            dailyActivityItem.getActivity().getMet());

    responseDto.setCaloriesBurned((int) calories);
  }
}
