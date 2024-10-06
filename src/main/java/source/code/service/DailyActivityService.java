package source.code.service;

import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.helper.CalculationsHelper;
import source.code.helper.JsonPatchHelper;
import source.code.dto.request.DailyCartActivityCreateDto;
import source.code.helper.ValidationHelper;
import source.code.model.Activity;
import source.code.model.DailyActivity;
import source.code.model.DailyCartActivity;
import source.code.model.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;
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
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final JsonPatchHelper jsonPatchHelper;
    private final DailyActivityRepository dailyActivityRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
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
    public void addActivityToDailyActivities(int userId, Integer activityId, DailyCartActivityCreateDto dto) {
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

        DailyCartActivityCreateDto dailyCartActivityDto = new DailyCartActivityCreateDto();
        dailyCartActivityDto.setTime(dailyCartActivity.getTime());

        DailyCartActivityCreateDto patchedDailyCartActivityDto = jsonPatchHelper.applyPatch(patch, dailyCartActivityDto, DailyCartActivityCreateDto.class);

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
        DailyActivity newDailyActivity = new DailyActivity(user, LocalDate.now());
        return dailyActivityRepository.save(newDailyActivity);
    }

    public DailyActivitiesResponseDto getActivitiesInCart(int userId) {
        DailyActivity dailyActivity = getDailyActivityByUserId(userId);

        User user = dailyActivity.getUser();

        List<ActivityCalculatedResponseDto> activities = dailyActivity.getDailyCartActivities().stream()
                .map(dailyCartActivity -> {
                    Activity activity = dailyCartActivity.getActivity();
                    return new ActivityCalculatedResponseDto(
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
        int totalCaloriesBurned = activities.stream().mapToInt(ActivityCalculatedResponseDto::getCaloriesBurned).sum();
        return new DailyActivitiesResponseDto(totalCaloriesBurned, activities);
    }
}
