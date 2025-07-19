package source.code.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.activity.DailyActivitiesGetDto;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.request.activity.DailyActivityItemUpdateDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.daily.DailyActivitiesResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.daily.DailyCartActivity;
import source.code.model.daily.DailyCart;
import source.code.model.user.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityItemRepository;
import source.code.repository.DailyCartRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.daily.DailyActivityService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyActivityServiceImpl implements DailyActivityService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final DailyActivityMapper dailyActivityMapper;
    private final RepositoryHelper repositoryHelper;
    private final DailyCartRepository dailyCartRepository;
    private final DailyActivityItemRepository dailyActivityItemRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public DailyActivityServiceImpl(
            DailyCartRepository dailyCartRepository,
            UserRepository userRepository,
            ActivityRepository activityRepository,
            JsonPatchService jsonPatchService,
            ValidationService validationService,
            DailyActivityMapper dailyActivityMapper,
            RepositoryHelper repositoryHelper,
            DailyActivityItemRepository dailyActivityItemRepository) {
        this.dailyCartRepository = dailyCartRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.dailyActivityMapper = dailyActivityMapper;
        this.repositoryHelper = repositoryHelper;
        this.dailyActivityItemRepository = dailyActivityItemRepository;
    }

    @Override
    @Transactional
    public void addActivityToDailyCart(int activityId, DailyActivityItemCreateDto dto) {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getOrCreateDailyCartForUser(userId, dto.getDate());
        Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);

        updateOrAddDailyActivityItem(dailyCart, activity, dto.getTime());
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public void removeActivityFromDailyCart(int dailyActivityItemId) {
        DailyCartActivity dailyCartActivity = getDailyCartActivity(dailyActivityItemId);
        dailyActivityItemRepository.delete(dailyCartActivity);
    }

    @Override
    @Transactional
    public void updateDailyActivityItem(int dailyActivityItemId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        DailyCartActivity dailyCartActivity = getDailyCartActivity(dailyActivityItemId);

        DailyActivityItemUpdateDto patchedDto = applyPatchToDailyActivityItem(dailyCartActivity, patch);
        validationService.validate(patchedDto);

        updateTime(dailyCartActivity, patchedDto.getTime());
        dailyActivityItemRepository.save(dailyCartActivity);
    }

    @Override
    @Transactional
    public DailyActivitiesResponseDto getActivitiesFromDailyCart(DailyActivitiesGetDto request) {
        int userId = AuthorizationUtil.getUserId();
        Optional<DailyCart> dailyCartOptional = getDailyCart(userId, request.getDate());

        if (dailyCartOptional.isEmpty()) {
            return DailyActivitiesResponseDto.of(Collections.emptyList(), 0);
        }

        DailyCart dailyCart = dailyCartOptional.get();

        User user = dailyCart.getUser();

        return dailyCart.getDailyCartActivities().stream()
                .map(dailyActivityItem -> dailyActivityMapper.toActivityCalculatedResponseDto(
                        dailyActivityItem,
                        user.getWeight()
                ))
                .collect(Collectors.teeing(
                        Collectors.toList(),
                        Collectors.summingInt(ActivityCalculatedResponseDto::getCaloriesBurned),
                        DailyActivitiesResponseDto::of
                ));
    }

    private void updateOrAddDailyActivityItem(
            DailyCart dailyCart,
            Activity activity,
            int time
    ) {
        dailyActivityItemRepository.findByDailyCartIdAndActivityId(dailyCart.getId(), activity.getId())
                .ifPresentOrElse(
                        foundItem -> foundItem.setTime(foundItem.getTime() + time),
                        () -> {
                            DailyCartActivity newItem = DailyCartActivity.of(activity, dailyCart, time);
                            dailyCart.getDailyCartActivities().add(newItem);
                        }
                );
    }

    private void updateTime(DailyCartActivity dailyCartActivity, int time) {
        dailyCartActivity.setTime(time);
    }

    private DailyCart createDailyCart(int userId, LocalDate date) {
        User user = repositoryHelper.find(userRepository, User.class, userId);
        return dailyCartRepository.save(DailyCart.of(user, date));
    }

    private DailyActivityItemUpdateDto applyPatchToDailyActivityItem(
            DailyCartActivity dailyCartActivity,
            JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        DailyActivityItemUpdateDto updateDto = DailyActivityItemUpdateDto.of(dailyCartActivity.getTime());
        return jsonPatchService.applyPatch(patch, updateDto, DailyActivityItemUpdateDto.class);
    }

    private DailyCartActivity getDailyCartActivity(int dailyActivityItemId) {
        return repositoryHelper.find(dailyActivityItemRepository, DailyCartActivity.class, dailyActivityItemId);
    }

    private DailyCart getOrCreateDailyCartForUser(int userId, LocalDate date) {
        return getDailyCart(userId, date)
                .orElseGet(() -> createDailyCart(userId, date));
    }

    private Optional<DailyCart> getDailyCart(int userId, LocalDate date) {
        return dailyCartRepository.findByUserIdAndDate(userId, date);
    }
}
