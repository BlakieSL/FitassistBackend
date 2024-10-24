package source.code.service.implementation.Daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import source.code.dto.request.Activity.DailyActivityItemCreateDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.Activity.DailyActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.Activity.DailyActivity;
import source.code.model.Activity.DailyActivityItem;
import source.code.model.User.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityItemRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.Daily.DailyActivityService;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyActivityServiceImpl implements DailyActivityService {
  private final JsonPatchService jsonPatchService;
  private final ValidationService validationService;
  private final DailyActivityMapper dailyActivityMapper;
  private final RepositoryHelper repositoryHelper;
  private final DailyActivityRepository dailyActivityRepository;
  private final DailyActivityItemRepository dailyActivityItemRepository;
  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  public DailyActivityServiceImpl(
          DailyActivityRepository dailyActivityRepository,
          UserRepository userRepository,
          ActivityRepository activityRepository,
          JsonPatchServiceImpl jsonPatchService,
          ValidationService validationService,
          DailyActivityMapper dailyActivityMapper,
          RepositoryHelper repositoryHelper,
          DailyActivityItemRepository dailyActivityItemRepository) {
    this.dailyActivityRepository = dailyActivityRepository;
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
    this.jsonPatchService = jsonPatchService;
    this.validationService = validationService;
    this.dailyActivityMapper = dailyActivityMapper;
    this.repositoryHelper = repositoryHelper;
    this.dailyActivityItemRepository = dailyActivityItemRepository;
  }

  @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
  @Transactional
  public void resetDailyCarts() {
    List<DailyActivity> carts = dailyActivityRepository.findAll();

    for (DailyActivity cart : carts) {
      resetDailyActivity(cart);
      dailyActivityRepository.save(cart);
    }
  }

  @Transactional
  public void addActivityToDailyActivityItem(int userId, Integer activityId,
                                             DailyActivityItemCreateDto dto) {

    DailyActivity dailyActivity = getOrCreateDailyActivityForUser(userId);
    Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);

    DailyActivityItem dailyActivityItem = getOrCreateDailyActivityItem(
            dailyActivity, activity, dto.getTime());
    updateOrAddDailyActivityItem(dailyActivity, dailyActivityItem);

    dailyActivityRepository.save(dailyActivity);
  }

  @Transactional
  public void removeActivityFromDailyActivity(int userId, int activityId) {
    DailyActivity dailyActivity = getOrCreateDailyActivityForUser(userId);
    DailyActivityItem dailyActivityItem = getDailyActivityItem(dailyActivity.getId(), activityId);

    dailyActivity.getDailyActivityItems().remove(dailyActivityItem);
    dailyActivityRepository.save(dailyActivity);
  }

  @Transactional
  public void updateDailyActivityItem(int userId, int activityId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    DailyActivity dailyActivity = getOrCreateDailyActivityForUser(userId);
    DailyActivityItem dailyActivityItem = getDailyActivityItem(dailyActivity.getId(), activityId);

    DailyActivityItemCreateDto patchedDto = applyPatchToDailyActivityItem(dailyActivityItem, patch);
    validationService.validate(patchedDto);

    updateDailyActivityItemTime(dailyActivityItem, patchedDto.getTime());
    dailyActivityRepository.save(dailyActivity);
  }

  public DailyActivitiesResponseDto getActivitiesFromDailyActivity(int userId) {
    DailyActivity dailyActivity = getOrCreateDailyActivityForUser(userId);
    User user = dailyActivity.getUser();

    List<ActivityCalculatedResponseDto> activities = dailyActivity.getDailyActivityItems().stream()
            .map(dailyActivityItem -> dailyActivityMapper
                    .toActivityCalculatedResponseDto(dailyActivityItem, user.getWeight()))
            .toList();

    int totalCaloriesBurned = activities.stream()
            .mapToInt(ActivityCalculatedResponseDto::getCaloriesBurned)
            .sum();

    return new DailyActivitiesResponseDto(activities, totalCaloriesBurned);
  }

  private DailyActivityItem getOrCreateDailyActivityItem(DailyActivity dailyActivity,
                                                         Activity activity,
                                                         int time) {
    return dailyActivityItemRepository
            .findByDailyActivityIdAndActivityId(dailyActivity.getId(), activity.getId())
            .orElse(DailyActivityItem
                    .createWithActivityDailyActivityTime(activity, dailyActivity, time));
  }

  private void updateOrAddDailyActivityItem(DailyActivity dailyActivity,
                                            DailyActivityItem dailyActivityItem) {
    if(existByDailyActivityAndItem(dailyActivityItem, dailyActivity)) {
      saveDailyActivityItem(dailyActivity, dailyActivityItem);
    }
    updateDailyActivityItemTime(dailyActivityItem, dailyActivityItem.getTime());
  }

  private void updateDailyActivityItemTime(DailyActivityItem dailyActivityItem, int time) {
    dailyActivityItem.setTime(time);
  }

  private DailyActivity getOrCreateDailyActivityForUser(int userId) {
    return dailyActivityRepository.findByUserId(userId)
            .orElseGet(() -> createDailyActivity(userId));
  }

  @Transactional
  public DailyActivity createDailyActivity(int userId) {
    User user = repositoryHelper.find(userRepository, User.class, userId);
    return dailyActivityRepository.save(DailyActivity.createForToday(user));
  }

  private DailyActivityItemCreateDto applyPatchToDailyActivityItem(DailyActivityItem dailyActivityItem,
                                                                   JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    DailyActivityItemCreateDto createDto = new DailyActivityItemCreateDto(dailyActivityItem.getTime());
    return jsonPatchService.applyPatch(patch, createDto, DailyActivityItemCreateDto.class);
  }

  private void saveDailyActivityItem(DailyActivity dailyActivity,
                                     DailyActivityItem dailyActivityItem) {
    dailyActivity.getDailyActivityItems().add(dailyActivityItem);
  }

  private DailyActivityItem getDailyActivityItem(int dailyActivityId, int activityId) {
    return dailyActivityItemRepository.findByDailyActivityIdAndActivityId(dailyActivityId, activityId)
            .orElseThrow(() -> new RecordNotFoundException(DailyActivityItem.class, activityId));
  }

  private boolean existByDailyActivityAndItem(DailyActivityItem dailyActivityItem,
                                              DailyActivity dailyActivity) {
    return dailyActivityItemRepository
            .existsByIdAndDailyActivityId(dailyActivityItem.getId(), dailyActivity.getId());
  }
  private void resetDailyActivity(DailyActivity dailyActivity) {
    dailyActivity.setDate(LocalDate.now());
    dailyActivity.getDailyActivityItems().clear();
  }
}
