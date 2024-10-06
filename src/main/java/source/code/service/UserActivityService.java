package source.code.service;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.model.Activity;
import source.code.model.User;
import source.code.model.UserActivity;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public UserActivityService(
            final UserActivityRepository userActivityRepository,
            final ActivityRepository activityRepository,
            final UserRepository userRepository){
        this.userActivityRepository = userActivityRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveActivityToUser(int activityId, int userId, short type) {
        if(userActivityRepository.existsByUserIdAndActivityIdAndType(userId, activityId, type)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId +
                            " already has activity with id: " + activityId +
                            " and type: " + type);
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + userId + " not found"));

        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity with id: " + activityId + " not found"));

        UserActivity userActivity = new UserActivity();
        userActivity.setUser(user);
        userActivity.setActivity(activity);
        userActivity.setType(type);
        userActivityRepository.save(userActivity);
    }

    @Transactional
    public void deleteSavedActivityFromUser(int activityId, int userId, short type) {
        UserActivity userActivity = userActivityRepository
                .findByUserIdAndActivityIdAndType(userId, activityId, type)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserActivity with user id: " + userId +
                                ", activity id: " + activityId +
                                " and type: " + type + " not found"));

        userActivityRepository.delete(userActivity);
    }

    public LikesAndSavesResponseDto calculateLikesAndSavesByActivityId(int activityId) {
        activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Activity with id: " + activityId + " not found"));

        long saves = userActivityRepository.countByActivityIdAndType(activityId, (short) 1);
        long likes = userActivityRepository.countByActivityIdAndType(activityId, (short) 2);
        return new LikesAndSavesResponseDto(likes, saves);
    }
}
