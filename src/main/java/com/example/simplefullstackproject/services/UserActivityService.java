package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.exceptions.NotUniqueRecordException;
import com.example.simplefullstackproject.models.Activity;
import com.example.simplefullstackproject.models.User;
import com.example.simplefullstackproject.models.UserActivity;
import com.example.simplefullstackproject.repositories.ActivityRepository;
import com.example.simplefullstackproject.repositories.UserActivityRepository;
import com.example.simplefullstackproject.repositories.UserRepository;
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
    public void addActivityToUser(Integer activityId, Integer userId) {
        if(userActivityRepository.existsByUserIdAndActivityId(userId, activityId)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId + " already has activity with id: " + activityId);
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
        userActivityRepository.save(userActivity);
    }

    @Transactional
    public void deleteActivityFromUser(Integer activityId, Integer userId) {
        UserActivity userActivity = userActivityRepository
                .findByUserIdAndActivityId(userId, activityId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserActivity with user id: " + userId +
                                " and activity id: " + activityId + " not found"));

        userActivityRepository.delete(userActivity);
    }
}
