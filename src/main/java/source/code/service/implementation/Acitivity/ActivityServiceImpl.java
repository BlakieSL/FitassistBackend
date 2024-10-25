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
import source.code.mapper.Activity.ActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.User.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.Activity.ActivityService;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Helpers.ValidationService;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
  private final RepositoryHelper repositoryHelper;
  private final ActivityMapper activityMapper;
  private final ValidationService validationService;
  private final JsonPatchService jsonPatchService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  public ActivityServiceImpl(
          RepositoryHelper repositoryHelper,
          ActivityMapper activityMapper,
          ValidationService validationService,
          JsonPatchService jsonPatchService,
          ApplicationEventPublisher applicationEventPublisher,
          ActivityRepository activityRepository,
          UserRepository userRepository) {
    this.repositoryHelper = repositoryHelper;
    this.activityMapper = activityMapper;
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public ActivityResponseDto createActivity(ActivityCreateDto dto) {
    Activity activity = activityRepository.save(activityMapper.toEntity(dto));
    publishEvent(new ActivityCreateEvent(this, activity));

    return activityMapper.toResponseDto(activity);
  }

  @Transactional
  public void updateActivity(int activityId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);
    ActivityUpdateDto patchedActivityUpdateDto = applyPatchToActivity(activity, patch);

    validationService.validate(patchedActivityUpdateDto);

    activityMapper.updateActivityFromDto(activity,patchedActivityUpdateDto);
    Activity savedActivity = activityRepository.save(activity);

    publishEvent(new ActivityUpdateEvent(this, savedActivity));
  }

  @Transactional
  public void deleteActivity(int activityId) {
    Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);
    activityRepository.delete(activity);

    publishEvent(new ActivityDeleteEvent(this, activity));
  }

  public ActivityCalculatedResponseDto calculateCaloriesBurned(
          int activityId, CalculateActivityCaloriesRequestDto request) {

    User user = repositoryHelper.find(userRepository, User.class, request.getUserId());
    Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);

    return activityMapper.toCalculatedDto(activity, user, request.getTime());
  }

  @Cacheable(value = "activities", key = "#id")
  public ActivityResponseDto getActivity(int activityId) {
    Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);
    return activityMapper.toResponseDto(activity);
  }

  @Cacheable(value = "allActivities")
  public List<ActivityResponseDto> getAllActivities() {
    return repositoryHelper.findAll(activityRepository, activityMapper::toResponseDto);
  }

  @Cacheable(value = "activitiesByCategory", key = "#categoryId")
  public List<ActivityResponseDto> getActivitiesByCategory(int categoryId) {
    return activityRepository.findAllByActivityCategory_Id(categoryId).stream()
            .map(activityMapper::toResponseDto)
            .toList();
  }

  public ActivityAverageMetResponseDto getAverageMet() {
    double averageMet = activityRepository.findAll().stream()
            .mapToDouble(Activity::getMet)
            .average()
            .orElse(0.0);

    return new ActivityAverageMetResponseDto(averageMet);
  }

  private ActivityUpdateDto applyPatchToActivity(Activity activity, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ActivityResponseDto responseDto = activityMapper.toResponseDto(activity);
    return jsonPatchService.applyPatch(patch, responseDto, ActivityUpdateDto.class);
  }

  private void publishEvent(Object event) {
    applicationEventPublisher.publishEvent(event);
  }
}