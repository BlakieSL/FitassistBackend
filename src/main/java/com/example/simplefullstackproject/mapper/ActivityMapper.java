package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.ActivityAdditionDto;
import com.example.simplefullstackproject.dto.ActivityCalculatedDto;
import com.example.simplefullstackproject.dto.ActivityCategoryDto;
import com.example.simplefullstackproject.dto.ActivitySummaryDto;
import com.example.simplefullstackproject.model.Activity;
import com.example.simplefullstackproject.model.ActivityCategory;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.repository.ActivityCategoryRepository;
import com.example.simplefullstackproject.helper.CalculationsHelper;
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
    public abstract ActivitySummaryDto toSummaryDto(Activity activity);

    @Mapping(target = "categoryName", source = "activityCategory.name")
    public abstract ActivityCalculatedDto toCalculatedDto(Activity activity, @Context User user, @Context int time);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activityCategory", source = "categoryName", qualifiedByName = "categoryNameToActivityCategory")
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
    @Named("categoryNameToActivityCategory")
    protected ActivityCategory categoryNameToActivityCategory(String categoryName) {
        return activityCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity category with name: " + categoryName + " not found"));
    }
}