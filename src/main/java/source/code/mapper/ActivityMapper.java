package source.code.mapper;

import source.code.dto.ActivityAdditionDto;
import source.code.dto.ActivityCalculatedDto;
import source.code.dto.ActivityCategoryDto;
import source.code.dto.ActivitySummaryDto;
import source.code.model.Activity;
import source.code.model.ActivityCategory;
import source.code.model.User;
import source.code.repository.ActivityCategoryRepository;
import source.code.helper.CalculationsHelper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {
    @Autowired
    private ActivityCategoryRepository activityCategoryRepository;

    @Autowired
    private CalculationsHelper calculationsHelper;

    @Mapping(target = "categoryName", source = "activityCategory.name")
    @Mapping(target = "categoryId", source = "activityCategory.id")
    public abstract ActivitySummaryDto toSummaryDto(Activity activity);

    @Mapping(target = "categoryName", source = "activityCategory.name")
    @Mapping(target = "categoryId", source = "activityCategory.id")
    @Mapping(target = "caloriesBurned", ignore = true)
    @Mapping(target = "time", ignore = true)
    public abstract ActivityCalculatedDto toCalculatedDto(Activity activity, @Context User user, @Context int time);

    @Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyCartActivities", ignore = true)
    @Mapping(target = "userActivities", ignore = true)

    public abstract Activity toEntity(ActivityAdditionDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    public abstract ActivityCategoryDto toCategoryDto(ActivityCategory activityCategory);

    @AfterMapping
    protected void setCaloriesBurned(@MappingTarget ActivityCalculatedDto dto, Activity activity, @Context User user, @Context int time) {
        int caloriesBurned = (int) calculationsHelper.calculateCaloriesBurned(time, user.getWeight(), activity.getMet());
        dto.setCaloriesBurned(caloriesBurned);
        dto.setTime(time);
    }

    @Named("categoryIdToActivityCategory")
    protected ActivityCategory categoryIdToActivityCategory(int categoryId) {
        return activityCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity category with id: " + categoryId + " not found"));
    }
}