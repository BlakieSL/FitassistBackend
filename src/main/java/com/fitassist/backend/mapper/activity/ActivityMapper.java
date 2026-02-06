package com.fitassist.backend.mapper.activity;

import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.ActivityUpdateDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.activity.Activity;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ActivityMapper {

	@Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "images", source = "mediaList", qualifiedByName = "mapMediaListToImagesDto")
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ActivityResponseDto toResponse(Activity activity);

	@Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ActivitySummaryDto toSummary(Activity activity);

	@Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "dailyItemId", ignore = true)
	@Mapping(target = "caloriesBurned", ignore = true)
	@Mapping(target = "time", ignore = true)
	@Mapping(target = "weight", ignore = true)
	public abstract ActivityCalculatedResponseDto toCalculated(Activity activity);

	@Mapping(target = "activityCategory", expression = "java(context.getCategory())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartActivities", ignore = true)
	@Mapping(target = "userActivities", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Activity toEntity(ActivityCreateDto dto, @Context ActivityMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "activityCategory", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartActivities", ignore = true)
	@Mapping(target = "userActivities", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void update(@MappingTarget Activity activity, ActivityUpdateDto request,
			@Context ActivityMappingContext context);

	@AfterMapping
	protected void updateCategory(@MappingTarget Activity activity, ActivityUpdateDto request,
			@Context ActivityMappingContext context) {
		Optional.ofNullable(context.getCategory()).ifPresent(activity::setActivityCategory);
	}

}
