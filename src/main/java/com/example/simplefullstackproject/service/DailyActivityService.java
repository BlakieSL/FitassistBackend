package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.ActivityCalculatedDto;
import com.example.simplefullstackproject.dto.DailyActivitiesResponse;
import com.example.simplefullstackproject.helper.CalculationsHelper;
import com.example.simplefullstackproject.helper.JsonPatchHelper;
import com.example.simplefullstackproject.dto.DailyActivityDto;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.model.Activity;
import com.example.simplefullstackproject.model.DailyActivity;
import com.example.simplefullstackproject.model.DailyCartActivity;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.repository.ActivityRepository;
import com.example.simplefullstackproject.repository.DailyActivityRepository;
import com.example.simplefullstackproject.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyActivityService {
    private final DailyActivityRepository dailyActivityRepository;
    private final ValidationHelper validationHelper;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final CalculationsHelper calculationsHelper;
    private final JsonPatchHelper jsonPatchHelper;
    public DailyActivityService(
            DailyActivityRepository dailyActivityRepository,
            UserRepository userRepository,
            ActivityRepository activityRepository,
            CalculationsHelper calculationsHelper,
            ValidationHelper validationHelper,
            JsonPatchHelper jsonPatchHelper) {
        this.dailyActivityRepository = dailyActivityRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
        this.jsonPatchHelper = jsonPatchHelper;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
    @Transactional
    public void updateDailyCarts() {
        List<DailyActivity> dailyActivities = dailyActivityRepository.findAll();
        LocalDate today = LocalDate.now();
        for (DailyActivity dailyActivity : dailyActivities) {
            dailyActivity.setDate(today);
            dailyActivity.getDailyCartActivities().clear();
            dailyActivityRepository.save(dailyActivity);
        }
    }

    @Transactional
    public void addActivityToDailyActivities(int userId, Integer activityId, DailyActivityDto dto) {
        validationHelper.validate(dto);

        DailyActivity dailyActivity = getDailyActivityByUserId(userId);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("Activity with id: " + activityId + " not found"));
        Optional<DailyCartActivity> existingDailyCartActivity = dailyActivity.getDailyCartActivities().stream()
                .filter(item -> item.getActivity().getId().equals(activityId))
                .findFirst();

        if (existingDailyCartActivity.isPresent()) {
            DailyCartActivity dailyCartActivity = existingDailyCartActivity.get();
            dailyCartActivity.setTime(dailyCartActivity.getTime() + dto.getTime());
        } else {
            DailyCartActivity dailyCartActivity = new DailyCartActivity();
            dailyCartActivity.setDailyCartActivity(dailyActivity);
            dailyCartActivity.setActivity(activity);
            dailyCartActivity.setTime(dto.getTime());
            dailyActivity.getDailyCartActivities().add(dailyCartActivity);
        }
        dailyActivityRepository.save(dailyActivity);
    }

    @Transactional
    public void removeActivityFromCart(int userId, int activityId) {
        DailyActivity dailyActivity = getDailyActivityByUserId(userId);
        DailyCartActivity dailyCartActivity = dailyActivity.getDailyCartActivities().stream()
                .filter(item -> item.getActivity().getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Activity with id: " + activityId + " not found"));
        dailyActivity.getDailyCartActivities().remove(dailyCartActivity);
        dailyActivityRepository.save(dailyActivity);
    }

    @Transactional
    public void modifyDailyCartActivities(int userId, int activityId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        DailyActivity dailyActivity = getDailyActivityByUserId(userId);

        DailyCartActivity dailyCartActivity = dailyActivity.getDailyCartActivities().stream()
                .filter(item -> item.getActivity().getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Activity with id: " + activityId + " not found in daily cart"));

        DailyActivityDto dailyCartActivityDto = new DailyActivityDto();
        dailyCartActivityDto.setTime(dailyCartActivity.getTime());

        DailyActivityDto patchedDailyCartActivityDto = jsonPatchHelper.applyPatch(patch, dailyCartActivityDto, DailyActivityDto.class);

        dailyCartActivity.setTime(patchedDailyCartActivityDto.getTime());
        dailyActivityRepository.save(dailyActivity);
    }



    private DailyActivity getDailyActivityByUserId(int userId) {
        return dailyActivityRepository.findByUserId(userId)
                .orElseGet(() -> createNewDailyActivityForUser(userId));
    }

    private DailyActivity createNewDailyActivityForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " not found"));
        DailyActivity newDailyActivity = new DailyActivity();
        newDailyActivity.setUser(user);
        newDailyActivity.setDate(LocalDate.now());
        return dailyActivityRepository.save(newDailyActivity);
    }

    public DailyActivitiesResponse getActivitiesInCart(int userId) {
        DailyActivity dailyActivity = getDailyActivityByUserId(userId);

        User user = dailyActivity.getUser();

        List<ActivityCalculatedDto> activities = dailyActivity.getDailyCartActivities().stream()
                .map(dailyCartActivity -> {
                    Activity activity = dailyCartActivity.getActivity();
                    return new ActivityCalculatedDto(
                            activity.getId(),
                            activity.getName(),
                            activity.getMet(),
                            activity.getActivityCategory().getName(),
                            activity.getActivityCategory().getId(),
                            (int) (calculationsHelper.calculateCaloriesBurned(dailyCartActivity.getTime(), user.getWeight(), activity.getMet())),
                            dailyCartActivity.getTime()
                    );
                })
                .collect(Collectors.toList());
        int totalCaloriesBurned = activities.stream().mapToInt(ActivityCalculatedDto::getCaloriesBurned).sum();
        return new DailyActivitiesResponse(totalCaloriesBurned, activities);
    }
}
