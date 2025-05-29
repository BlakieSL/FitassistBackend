package source.code.service.implementation.user.interaction.withType;

import org.springframework.stereotype.Service;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.profile.User;
import source.code.model.user.UserActivity;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.user.SavedService;

import java.util.List;

@Service("userActivityService")
public class UserActivityServiceImpl
        extends GenericSavedService<Activity, UserActivity, ActivityResponseDto>
        implements SavedService {

    public UserActivityServiceImpl(UserActivityRepository userActivityRepository,
                                   ActivityRepository activityRepository,
                                   UserRepository userRepository,
                                   ActivityMapper activityMapper) {
        super(userRepository,
                activityRepository,
                userActivityRepository,
                activityMapper::toResponseDto,
                Activity.class);
    }

    @Override
    protected boolean isAlreadySaved(int userId, int activityId, short type) {
        return ((UserActivityRepository) userEntityRepository)
                .existsByUserIdAndActivityIdAndType(userId, activityId, type);
    }

    @Override
    protected UserActivity createUserEntity(User user, Activity entity, short type) {
        return UserActivity.of(user, entity, type);
    }

    @Override
    protected UserActivity findUserEntity(int userId, int activityId, short type) {
        return ((UserActivityRepository) userEntityRepository)
                .findByUserIdAndActivityIdAndType(userId, activityId, type)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserActivity.class,
                        userId,
                        activityId,
                        type
                ));
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
        return ((UserActivityRepository) userEntityRepository)
                .countByActivityIdAndType(activityId, (short) 1);
    }

    @Override
    protected long countLikes(int activityId) {
        return ((UserActivityRepository) userEntityRepository)
                .countByActivityIdAndType(activityId, (short) 2);
    }
}
