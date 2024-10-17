package source.code.service.implementation.Acitivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import source.code.dto.request.DailyActivityItemCreateDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.mapper.Activity.DailyActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.Activity.DailyActivity;
import source.code.model.Activity.DailyActivityItem;
import source.code.model.User.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.DailyActivityService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyActivityServiceImpl implements DailyActivityService {
  private final JsonPatchHelper jsonPatchHelper;
  private final DailyActivityMapper dailyActivityMapper;
  private final DailyActivityRepository dailyActivityRepository;
  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  public DailyActivityServiceImpl(
          DailyActivityRepository dailyActivityRepository,
          UserRepository userRepository,
          ActivityRepository activityRepository,
          JsonPatchHelper jsonPatchHelper,
          DailyActivityMapper dailyActivityMapper) {
    this.dailyActivityRepository = dailyActivityRepository;
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
    this.jsonPatchHelper = jsonPatchHelper;
    this.dailyActivityMapper = dailyActivityMapper;
  }

  @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
  @Transactional
  public void resetDailyCarts() {
    List<DailyActivity> dailyActivities = dailyActivityRepository.findAll();

    for (DailyActivity dailyActivity : dailyActivities) {
      resetDailyActivity(dailyActivity);
      dailyActivityRepository.save(dailyActivity);
    }
  }


  private void resetDailyActivity(DailyActivity dailyActivity) {
    dailyActivity.setDate(LocalDate.now());
    dailyActivity.getDailyActivityItems().clear();
  }

  @Transactional
  public void addActivityToDailyActivityItem(int userId, Integer activityId,
                                             DailyActivityItemCreateDto dto) {

    DailyActivity dailyActivity = getDailyActivityByUser(userId);

    Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Activity with id: " + activityId + " not found"));

    Optional<DailyActivityItem> existingDailyActivityItem =
            dailyActivity.getDailyActivityItems().stream()
                    .filter(item -> item.getActivity().getId().equals(activityId))
                    .findFirst();

    if (existingDailyActivityItem.isPresent()) {
      updateDailyActivityItemTime(existingDailyActivityItem.get(), dto.getTime());
    } else {
      DailyActivityItem dailyActivityItem = DailyActivityItem
              .createWithActivityDailyActivityTime(activity, dailyActivity, dto.getTime());

      saveDailyActivityItem(dailyActivity, dailyActivityItem);
    }
    dailyActivityRepository.save(dailyActivity);
  }

  private DailyActivity getDailyActivityByUser(int userId) {
    return dailyActivityRepository.findByUserId(userId)
            .orElseGet(() -> createNewDailyActivityForUser(userId));
  }

  private void updateDailyActivityItemTime(DailyActivityItem dailyActivityItem, int time) {
    dailyActivityItem.setTime(time);
  }

  private void saveDailyActivityItem(DailyActivity dailyActivity,
                                     DailyActivityItem dailyActivityItem) {
    dailyActivity.getDailyActivityItems().add(dailyActivityItem);
  }

  @Transactional
  public void removeActivityFromDailyActivity(int userId, int activityId) {
    DailyActivity dailyActivity = getDailyActivityByUser(userId);
    DailyActivityItem dailyActivityItem = dailyActivity.getDailyActivityItems().stream()
            .filter(item -> item.getActivity().getId().equals(activityId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(
                    "Activity with id: " + activityId + " not found"));
    dailyActivity.getDailyActivityItems().remove(dailyActivityItem);
    dailyActivityRepository.save(dailyActivity);
  }

  @Transactional
  public void updateDailyActivityItem(int userId, int activityId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    DailyActivity dailyActivity = getDailyActivityByUser(userId);

    DailyActivityItem dailyActivityItem = dailyActivity.getDailyActivityItems().stream()
            .filter(item -> item.getActivity().getId().equals(activityId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(
                    "Activity with id: " + activityId + " not found in daily cart"));

    DailyActivityItemCreateDto dailyActivityItemDto = new DailyActivityItemCreateDto();
    dailyActivityItemDto.setTime(dailyActivityItem.getTime());

    DailyActivityItemCreateDto patchedDailyActivityItemDto =
            jsonPatchHelper.applyPatch(
                    patch,
                    dailyActivityItemDto,
                    DailyActivityItemCreateDto.class);

    dailyActivityItem.setTime(patchedDailyActivityItemDto.getTime());
    dailyActivityRepository.save(dailyActivity);
  }

  @Transactional
  public DailyActivity createNewDailyActivityForUser(int userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));
    DailyActivity newDailyActivity = DailyActivity.createForToday(user);

    return dailyActivityRepository.save(newDailyActivity);
  }


  public DailyActivitiesResponseDto getActivitiesFromDailyActivity(int userId) {
    DailyActivity dailyActivity = getDailyActivityByUser(userId);
    User user = dailyActivity.getUser();

    List<ActivityCalculatedResponseDto> activities = dailyActivity.getDailyActivityItems().stream()
            .map(dailyActivityItem -> dailyActivityMapper
                    .toActivityCalculatedResponseDto(dailyActivityItem, user.getWeight()))
            .collect(Collectors.toList());

    int totalCaloriesBurned = activities.stream()
            .mapToInt(ActivityCalculatedResponseDto::getCaloriesBurned)
            .sum();

    return new DailyActivitiesResponseDto(activities, totalCaloriesBurned);
  }
}
