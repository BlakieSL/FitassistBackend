package source.code.mapper.daily;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    @Mapping(target = "id", source = "activity.id")
    @Mapping(target = "name", source = "activity.name")
    @Mapping(target = "met", source = "activity.met")
    @Mapping(target = "categoryName", source = "activity.activityCategory.name")
    @Mapping(target = "categoryId", source = "activity.activityCategory.id")
    @Mapping(target = "caloriesBurned", ignore = true)
    @Mapping(target = "time", source = "time")
    @Mapping(target = "weight", source = "weight")
    public abstract ActivityCalculatedResponseDto toActivityCalculatedResponseDto(
            DailyCartActivity dailyCartActivity);

    @AfterMapping
    protected void setCaloriesBurned(
            @MappingTarget ActivityCalculatedResponseDto responseDto,
            DailyCartActivity dailyCartActivity) {

        BigDecimal caloriesBurned = calculationsService.calculateCaloriesBurned(
                dailyCartActivity.getTime(),
                dailyCartActivity.getWeight(),
                dailyCartActivity.getActivity().getMet());

        responseDto.setCaloriesBurned(caloriesBurned.setScale(0, RoundingMode.HALF_UP).intValue());
    }
}
