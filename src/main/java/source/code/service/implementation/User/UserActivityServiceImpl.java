package source.code.service.implementation.User;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.ActivityResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.mapper.ActivityMapperImpl;
import source.code.model.Activity.Activity;
import source.code.model.User.User;
import source.code.model.User.UserActivity;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.UserActivityService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserActivityServiceImpl implements UserActivityService {
  private final UserActivityRepository userActivityRepository;
  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;
  private final ActivityMapperImpl activityMapper;

  public UserActivityServiceImpl(
          UserActivityRepository userActivityRepository,
          ActivityRepository activityRepository,
          UserRepository userRepository,
          ActivityMapperImpl activityMapper) {
    this.userActivityRepository = userActivityRepository;
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
    this.activityMapper = activityMapper;
  }

  @Transactional
  public void saveActivityToUser(int userId, int activityId, short type) {
    if (isAlreadySaved(userId, activityId, type)) {
      throw new NotUniqueRecordException(
              "User with id: " + userId
                      + " already has activity with id: " + activityId
                      + " and type: " + type);
    }

    User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));

    Activity activity = activityRepository
            .findById(activityId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Activity with id: " + activityId + " not found"));

    UserActivity userActivity =
            UserActivity.createWithUserActivityType(user, activity, type);
    userActivityRepository.save(userActivity);
  }

  @Transactional
  public void deleteSavedActivityFromUser(int activityId, int userId, short type) {
    UserActivity userActivity = userActivityRepository
            .findByUserIdAndActivityIdAndType(userId, activityId, type)
            .orElseThrow(() -> new NoSuchElementException(
                    "UserActivity with user id: " + userId
                            + ", activity id: " + activityId
                            + " and type: " + type + " not found"));

    userActivityRepository.delete(userActivity);
  }

  public List<ActivityResponseDto> getActivitiesByUserAndType(int userId, short type) {
    List<UserActivity> userActivities = userActivityRepository.findByUserIdAndType(userId, type);

    List<Activity> activities = userActivities.stream()
            .map(UserActivity::getActivity)
            .collect(Collectors.toList());

    return activities.stream()
            .map(activityMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public LikesAndSavesResponseDto calculateActivityLikesAndSaves(int activityId) {
    activityRepository.findById(activityId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Activity with id: " + activityId + " not found"));

    long saves = userActivityRepository.countByActivityIdAndType(activityId, (short) 1);
    long likes = userActivityRepository.countByActivityIdAndType(activityId, (short) 2);

    return new LikesAndSavesResponseDto(likes, saves);
  }

  private boolean isAlreadySaved(int userId, int activityId, short type) {
    return userActivityRepository.existsByUserIdAndActivityIdAndType(userId, activityId, type);
  }
}
