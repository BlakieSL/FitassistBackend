package source.code.mapper.daily;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.model.daily.DailyActivityItem;
import source.code.service.declaration.helpers.CalculationsService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public abstract class DailyActivityMapper {
    @Autowired
    private CalculationsService calculationsService;

    @Mapping(target = "id", source = "dailyActivityItem.activity.id")
    @Mapping(target = "name", source = "dailyActivityItem.activity.name")
    @Mapping(target = "met", source = "dailyActivityItem.activity.met")
    @Mapping(target = "categoryName", source = "dailyActivityItem.activity.activityCategory.name")
    @Mapping(target = "categoryId", source = "dailyActivityItem.activity.activityCategory.id")
    @Mapping(target = "caloriesBurned", ignore = true)
    @Mapping(target = "time", source = "dailyActivityItem.time")
    public abstract ActivityCalculatedResponseDto toActivityCalculatedResponseDto(
            DailyActivityItem dailyActivityItem, BigDecimal userWeight);

    @AfterMapping
    protected void setCaloriesBurned(
            @MappingTarget ActivityCalculatedResponseDto responseDto,
            DailyActivityItem dailyActivityItem,
            @Context BigDecimal userWeight) {

        BigDecimal caloriesBurned = calculationsService.calculateCaloriesBurned(
                dailyActivityItem.getTime(), userWeight, dailyActivityItem.getActivity().getMet());

        responseDto.setCaloriesBurned(caloriesBurned.setScale(0, RoundingMode.HALF_UP).intValue());
    }
}
