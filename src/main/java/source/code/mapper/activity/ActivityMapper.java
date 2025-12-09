package source.code.mapper.activity;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.ActivityUpdateDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.model.activity.Activity;
import source.code.model.activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.helpers.CalculationsService;
import source.code.service.declaration.helpers.RepositoryHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Mapper(componentModel = "spring")
public abstract class  ActivityMapper {
    @Autowired
    private ActivityCategoryRepository activityCategoryRepository;

    @Autowired
    private RepositoryHelper repositoryHelper;
    @Autowired
    private CalculationsService calculationsService;

    @Autowired
    private AwsS3Service awsS3Service;

    @Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapActivityCategoryToResponseDto")
    @Mapping(target = "firstImageUrl", ignore = true)
    public abstract ActivitySummaryDto toSummaryDto(Activity activity);

    @Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapActivityCategoryToResponseDto")
    @Mapping(target = "caloriesBurned", ignore = true)
    @Mapping(target = "time", ignore = true)
    public abstract ActivityCalculatedResponseDto toCalculatedDto(
            Activity activity, @Context BigDecimal weight, @Context int time);

    @Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyCartActivities", ignore = true)
    @Mapping(target = "userActivities", ignore = true)
    public abstract Activity toEntity(ActivityCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyCartActivities", ignore = true)
    @Mapping(target = "userActivities", ignore = true)
    public abstract void updateActivityFromDto(@MappingTarget Activity activity, ActivityUpdateDto request);

    @AfterMapping
    protected void setCaloriesBurned(
            @MappingTarget ActivityCalculatedResponseDto dto, Activity activity,
            @Context BigDecimal weight, @Context int time
    ) {
        BigDecimal caloriesBurned = calculationsService.calculateCaloriesBurned(
                time, weight, activity.getMet());

        int calories = caloriesBurned.setScale(0, RoundingMode.HALF_UP).intValue();

        dto.setCaloriesBurned(calories);
        dto.setTime(time);
        dto.setWeight(weight);
    }

    @Mapping(target = "category", source = "activityCategory", qualifiedByName = "mapActivityCategoryToResponseDto")
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "savesCount", ignore = true)
    @Mapping(target = "saved", ignore = true)
    public abstract ActivityResponseDto toDetailedResponseDto(Activity activity);

    @AfterMapping
    protected void mapImageUrls(@MappingTarget ActivityResponseDto dto, Activity activity) {
        List<String> imageUrls = activity.getMediaList().stream()
                .map(media -> awsS3Service.getImage(media.getImageName()))
                .toList();
        dto.setImageUrls(imageUrls);
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