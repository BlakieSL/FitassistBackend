package source.code.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.activity.DailyActivityItem;
import source.code.model.daily.DailyCart;
import source.code.model.user.profile.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityItemRepository;
import source.code.repository.DailyCartRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.daily.DailyActivityService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.time.LocalDate;
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
        DailyCart dailyCart = getOrCreateDailyCartForUser(userId);
        Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);

        updateOrAddDailyActivityItem(dailyCart, activity, dto.getTime());
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public void removeActivityFromDailyCart(int activityId) {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getDailyCartForUserOrThrow(userId);
        DailyActivityItem dailyActivityItem = getDailyActivityItem(dailyCart.getId(), activityId);

        dailyCart.getDailyActivityItems().remove(dailyActivityItem);
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public void updateDailyActivityItem(int activityId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getDailyCartForUserOrThrow(userId);
        DailyActivityItem dailyActivityItem = getDailyActivityItem(dailyCart.getId(), activityId);

        DailyActivityItemCreateDto patchedDto = applyPatchToDailyActivityItem(dailyActivityItem, patch);
        validationService.validate(patchedDto);

        updateTime(dailyActivityItem, patchedDto.getTime());
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public DailyActivitiesResponseDto getActivitiesFromDailyCart() {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getOrCreateDailyCartForUser(userId);
        User user = dailyCart.getUser();

        return dailyCart.getDailyActivityItems().stream()
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
                            DailyActivityItem newItem = DailyActivityItem.of(activity, dailyCart, time);
                            dailyCart.getDailyActivityItems().add(newItem);
                        }
                );
    }

    private DailyCart getOrCreateDailyCartForUser(int userId) {
        return dailyCartRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseGet(() -> createDailyCart(userId));
    }

    private DailyCart getDailyCartForUserOrThrow(int userId) {
        return dailyCartRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow(() -> RecordNotFoundException.of(DailyCart.class, userId));
    }

    private void updateTime(DailyActivityItem dailyActivityItem, int time) {
        dailyActivityItem.setTime(time);
    }

    private DailyCart createDailyCart(int userId) {
        User user = repositoryHelper.find(userRepository, User.class, userId);
        return dailyCartRepository.save(DailyCart.createForToday(user));
    }

    private DailyActivityItemCreateDto applyPatchToDailyActivityItem(
            DailyActivityItem dailyActivityItem,
            JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        DailyActivityItemCreateDto createDto = DailyActivityItemCreateDto.of(dailyActivityItem.getTime());
        return jsonPatchService.applyPatch(patch, createDto, DailyActivityItemCreateDto.class);
    }

    private DailyActivityItem getDailyActivityItem(int dailyActivityId, int activityId) {
        return dailyActivityItemRepository.
                findByDailyCartIdAndActivityId(dailyActivityId, activityId)
                .orElseThrow(() -> RecordNotFoundException.of(DailyActivityItem.class, activityId));
    }
}
