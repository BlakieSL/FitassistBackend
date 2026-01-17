package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.ActivityUpdateDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.mapper.helper.CommonMappingHelper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.activity.ActivityCategory;
import com.fitassist.backend.repository.ActivityCategoryRepository;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ActivityMapper {

	@Autowired
	private ActivityCategoryRepository activityCategoryRepository;

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Autowired
	private CalculationsService calculationsService;

	@Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapActivityCategoryToResponseDto")
	@Mapping(target = "images", source = "mediaList", qualifiedByName = "mapMediaListToImagesDto")
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ActivityResponseDto toDetailedResponseDto(Activity activity);

	@Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapActivityCategoryToResponseDto")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ActivitySummaryDto toSummaryDto(Activity activity);

	@Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapActivityCategoryToResponseDto")
	@Mapping(target = "caloriesBurned", ignore = true)
	@Mapping(target = "time", ignore = true)
	@Mapping(target = "dailyItemId", ignore = true)
	@Mapping(target = "weight", ignore = true)
	public abstract ActivityCalculatedResponseDto toCalculatedDto(Activity activity, @Context BigDecimal weight,
			@Context int time);

	@Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartActivities", ignore = true)
	@Mapping(target = "userActivities", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Activity toEntity(ActivityCreateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartActivities", ignore = true)
	@Mapping(target = "userActivities", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void updateActivityFromDto(@MappingTarget Activity activity, ActivityUpdateDto request);

	@AfterMapping
	protected void setCaloriesBurned(@MappingTarget ActivityCalculatedResponseDto dto, Activity activity,
			@Context BigDecimal weight, @Context int time) {
		BigDecimal caloriesBurned = calculationsService.calculateCaloriesBurned(time, weight, activity.getMet());

		dto.setCaloriesBurned(caloriesBurned);
		dto.setTime(time);
		dto.setWeight(weight);
	}

	@Named("categoryIdToActivityCategory")
	protected ActivityCategory categoryIdToActivityCategory(int categoryId) {
		return repositoryHelper.find(activityCategoryRepository, ActivityCategory.class, categoryId);
	}

	@Named("mapActivityCategoryToResponseDto")
	protected CategoryResponseDto mapActivityCategoryToResponseDto(ActivityCategory category) {
		return new CategoryResponseDto(category.getId(), category.getName());
	}

}
