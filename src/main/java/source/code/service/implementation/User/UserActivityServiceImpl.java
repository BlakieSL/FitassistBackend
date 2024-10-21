package source.code.service.implementation.User;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.ActivityResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.Activity.ActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.User.User;
import source.code.model.User.UserActivity;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.User.SavedService;
import source.code.service.declaration.User.UserActivityService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service("userActivityService")
public class UserActivityServiceImpl
        extends GenericSavedService<Activity, UserActivity, ActivityResponseDto>
        implements SavedService {

  public UserActivityServiceImpl(UserActivityRepository userActivityRepository,
                                 ActivityRepository activityRepository,
                                 UserRepository userRepository,
                                 ActivityMapper activityMapper) {
    super(userRepository, activityRepository, userActivityRepository, activityMapper::toResponseDto);
  }

  @Override
  protected boolean isAlreadySaved(int userId, int activityId, short type) {
    return ((UserActivityRepository) userEntityRepository)
            .existsByUserIdAndActivityIdAndType(userId, activityId, type);
  }

  @Override
  protected UserActivity createUserEntity(User user, Activity entity, short type) {
    return UserActivity.createWithUserActivityType(user, entity, type);
  }

  @Override
  protected UserActivity findUserEntity(int userId, int activityId, short type) {
    return ((UserActivityRepository) userEntityRepository)
            .findByUserIdAndActivityIdAndType(userId, activityId, type)
            .orElseThrow(() -> new RecordNotFoundException("UserActivity", userId, activityId, type));
  }

  @Override
  protected List<UserActivity> findAllByUserAndType(int userId, short type) {
    return ((UserActivityRepository) userEntityRepository).findByUserIdAndType(userId, type);
  }

  @Override
  protected Activity extractEntity(UserActivity userActivity) {
    return userActivity.getActivity();
  }

  @Override
  protected long countSaves(int activityId) {
    return ((UserActivityRepository) userEntityRepository).countByActivityIdAndType(activityId, (short) 1);
  }

  @Override
  protected long countLikes(int activityId) {
    return ((UserActivityRepository) userEntityRepository).countByActivityIdAndType(activityId, (short) 2);
  }
}
