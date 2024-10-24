package source.code.mapper.Activity;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.Activity.ActivityCreateDto;
import source.code.dto.request.Activity.ActivityUpdateDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.model.Activity.Activity;
import source.code.model.Activity.ActivityCategory;
import source.code.model.User.User;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.declaration.Helpers.CalculationsService;
import source.code.service.declaration.Helpers.RepositoryHelper;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {
  @Autowired
  private ActivityCategoryRepository activityCategoryRepository;

  @Autowired
  private RepositoryHelper repositoryHelper;

  @Autowired
  private CalculationsService calculationsService;

  @Mapping(target = "categoryName", source = "activityCategory.name")
  @Mapping(target = "categoryId", source = "activityCategory.id")
  public abstract ActivityResponseDto toResponseDto(Activity activity);

  @Mapping(target = "categoryName", source = "activityCategory.name")
  @Mapping(target = "categoryId", source = "activityCategory.id")
  @Mapping(target = "caloriesBurned", ignore = true)
  @Mapping(target = "time", ignore = true)
  public abstract ActivityCalculatedResponseDto toCalculatedDto(Activity activity, @Context User user, @Context int time);

  @Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "dailyActivityItems", ignore = true)
  @Mapping(target = "userActivities", ignore = true)
  public abstract Activity toEntity(ActivityCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "activityCategory", source = "categoryId", qualifiedByName = "categoryIdToActivityCategory")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "dailyActivityItems", ignore = true)
  @Mapping(target = "userActivities", ignore = true)
  public abstract void updateActivityFromDto(@MappingTarget Activity activity, ActivityUpdateDto request);

  @AfterMapping
  protected void setCaloriesBurned(@MappingTarget ActivityCalculatedResponseDto dto, Activity activity, @Context User user, @Context int time) {
    int caloriesBurned = (int) calculationsService.calculateCaloriesBurned(time, user.getWeight(), activity.getMet());
    dto.setCaloriesBurned(caloriesBurned);
    dto.setTime(time);
  }

  @Named("categoryIdToActivityCategory")
  protected ActivityCategory categoryIdToActivityCategory(int categoryId) {
    return repositoryHelper.find(activityCategoryRepository, ActivityCategory.class, categoryId);
  }
}