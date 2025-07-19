package source.code.mapper.daily;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.model.daily.DailyCartActivity;
import source.code.service.declaration.helpers.CalculationsService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public abstract class DailyActivityMapper {
    @Autowired
    private CalculationsService calculationsService;

    @Mapping(target = "id", source = "dailyCartActivity.activity.id")
    @Mapping(target = "name", source = "dailyCartActivity.activity.name")
    @Mapping(target = "met", source = "dailyCartActivity.activity.met")
    @Mapping(target = "categoryName", source = "dailyCartActivity.activity.activityCategory.name")
    @Mapping(target = "categoryId", source = "dailyCartActivity.activity.activityCategory.id")
    @Mapping(target = "caloriesBurned", ignore = true)
    @Mapping(target = "time", source = "dailyCartActivity.time")
    public abstract ActivityCalculatedResponseDto toActivityCalculatedResponseDto(
            DailyCartActivity dailyCartActivity, BigDecimal userWeight);

    @AfterMapping
    protected void setCaloriesBurned(
            @MappingTarget ActivityCalculatedResponseDto responseDto,
            DailyCartActivity dailyCartActivity,
            @Context BigDecimal userWeight) {

        BigDecimal caloriesBurned = calculationsService.calculateCaloriesBurned(
                dailyCartActivity.getTime(), userWeight, dailyCartActivity.getActivity().getMet());

        responseDto.setCaloriesBurned(caloriesBurned.setScale(0, RoundingMode.HALF_UP).intValue());
    }
}
