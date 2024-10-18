package source.code.service.implementation.Acitivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Activity.ActivityCreateEvent;
import source.code.cache.event.Activity.ActivityDeleteEvent;
import source.code.cache.event.Activity.ActivityUpdateEvent;
import source.code.dto.request.Activity.ActivityCreateDto;
import source.code.dto.request.Activity.ActivityUpdateDto;
import source.code.dto.request.Activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Activity.ActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.User.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.ActivityService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {
  private final ActivityMapper activityMapper;
  private final ValidationHelper validationHelper;
  private final JsonPatchHelper jsonPatchHelper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  public ActivityServiceImpl(
          ActivityMapper activityMapper,
          ValidationHelper validationHelper,
          JsonPatchHelper jsonPatchHelper,
          ApplicationEventPublisher applicationEventPublisher,
          ActivityRepository activityRepository,
          UserRepository userRepository) {
    this.activityMapper = activityMapper;
    this.validationHelper = validationHelper;
    this.jsonPatchHelper = jsonPatchHelper;
    this.applicationEventPublisher = applicationEventPublisher;
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public ActivityResponseDto createActivity(ActivityCreateDto dto) {
    Activity activity = activityRepository.save(activityMapper.toEntity(dto));
    applicationEventPublisher.publishEvent(new ActivityCreateEvent(this, dto));

    return activityMapper.toResponseDto(activity);
  }

  @Transactional
  public void updateActivity(int activityId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    Activity activity = getActivityOrThrow(activityId);
    ActivityUpdateDto patchedActivityUpdateDto = applyPatchToActivity(activity, patch);

    validationHelper.validate(patchedActivityUpdateDto);

    activityMapper.updateActivityFromDto(activity,patchedActivityUpdateDto);
    Activity savedActivity = activityRepository.save(activity);

    applicationEventPublisher.publishEvent(new ActivityUpdateEvent(this, savedActivity));
  }

  @Transactional
  public void deleteActivity(int activityId) {
    Activity activity = getActivityOrThrow(activityId);
    activityRepository.delete(getActivityOrThrow(activityId));

    applicationEventPublisher.publishEvent(new ActivityDeleteEvent(this, activity));
  }

  public ActivityCalculatedResponseDto calculateCaloriesBurned(
          int activityId,
          CalculateActivityCaloriesRequestDto request) {

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + request.getUserId() + " not found"));

    Activity activity = getActivityOrThrow(activityId);

    return activityMapper.toCalculatedDto(activity, user, request.getTime());
  }

  public List<ActivityResponseDto> searchActivities(SearchRequestDto request) {
    List<Activity> activities = activityRepository
            .findAllByNameContainingIgnoreCase(request.getName());

    return activities.stream()
            .map(activityMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "activities", key = "#id")
  public ActivityResponseDto getActivity(int activityId) {
    Activity activity = getActivityOrThrow(activityId);

    return activityMapper.toResponseDto(activity);
  }

  @Cacheable(value = "allActivities")
  public List<ActivityResponseDto> getAllActivities() {
    List<Activity> activities = activityRepository.findAll();

    return activities.stream()
            .map(activityMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "activitiesByCategory", key = "#categoryId")
  public List<ActivityResponseDto> getActivitiesByCategory(int categoryId) {
    List<Activity> activities = activityRepository.findAllByActivityCategory_Id(categoryId);

    return activities.stream()
            .map(activityMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public ActivityAverageMetResponseDto getAverageMet() {
    List<Activity> activities = activityRepository.findAll();

    double averageMet = activities.stream()
            .mapToDouble(Activity::getMet)
            .average()
            .orElse(0.0);

    return new ActivityAverageMetResponseDto(averageMet);
  }

  private Activity getActivityOrThrow(int activityId) {
    return activityRepository.findById(activityId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Activity with id: " + activityId + " not found"));
  }

  private ActivityUpdateDto applyPatchToActivity(Activity activity, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ActivityResponseDto responseDto = activityMapper.toResponseDto(activity);
    return jsonPatchHelper.applyPatch(patch, responseDto, ActivityUpdateDto.class);
  }
}