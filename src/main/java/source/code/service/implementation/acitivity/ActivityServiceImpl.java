package source.code.service.implementation.acitivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.ActivityUpdateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.event.events.Activity.ActivityCreateEvent;
import source.code.event.events.Activity.ActivityDeleteEvent;
import source.code.event.events.Activity.ActivityUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.User.AuthorizationUtil;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.activity.ActivityService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ActivitySpecification;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final RepositoryHelper repositoryHelper;
    private final ActivityMapper activityMapper;
    private final ValidationService validationService;
    private final JsonPatchService jsonPatchService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityServiceImpl(
            RepositoryHelper repositoryHelper,
            ActivityMapper activityMapper,
            ValidationService validationService,
            JsonPatchService jsonPatchService,
            ApplicationEventPublisher applicationEventPublisher,
            ActivityRepository activityRepository,
            UserRepository userRepository) {
        this.repositoryHelper = repositoryHelper;
        this.activityMapper = activityMapper;
        this.validationService = validationService;
        this.jsonPatchService = jsonPatchService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ActivityResponseDto createActivity(ActivityCreateDto dto) {
        Activity activity = activityRepository.save(activityMapper.toEntity(dto));
        publishEvent(new ActivityCreateEvent(this, activity));

        return activityMapper.toResponseDto(activity);
    }

    @Override
    @Transactional
    public void updateActivity(int activityId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Activity activity = findActivity(activityId);
        ActivityUpdateDto patchedActivityUpdateDto = applyPatchToActivity(activity, patch);

        validationService.validate(patchedActivityUpdateDto);
        activityMapper.updateActivityFromDto(activity, patchedActivityUpdateDto);
        Activity savedActivity = activityRepository.save(activity);

        publishEvent(new ActivityUpdateEvent(this, savedActivity));
    }

    @Override
    @Transactional
    public void deleteActivity(int activityId) {
        Activity activity = findActivity(activityId);
        activityRepository.delete(activity);

        publishEvent(new ActivityDeleteEvent(this, activity));
    }

    @Override
    public ActivityCalculatedResponseDto calculateCaloriesBurned(
            int activityId, CalculateActivityCaloriesRequestDto request)
    {
        int userId = AuthorizationUtil.getUserId();
        User user = findUser(userId);
        Activity activity = findActivity(activityId);

        return activityMapper.toCalculatedDto(activity, user, request.getTime());
    }

    @Override
    @Cacheable(value = CacheNames.ACTIVITIES, key = "#id")
    public ActivityResponseDto getActivity(int activityId) {
        Activity activity = findActivity(activityId);
        return activityMapper.toResponseDto(activity);
    }

    @Override
    @Cacheable(value = CacheNames.ALL_ACTIVITIES)
    public List<ActivityResponseDto> getAllActivities() {
        return repositoryHelper.findAll(activityRepository, activityMapper::toResponseDto);
    }

    @Override
    public List<ActivityResponseDto> getFilteredActivities(FilterDto filter) {
        SpecificationFactory<Activity> activityFactory = ActivitySpecification::of;
        SpecificationBuilder<Activity> specificationBuilder = SpecificationBuilder.of(
                filter,
                activityFactory
        );
        Specification<Activity> specification = specificationBuilder.build();

        return activityRepository.findAll(specification).stream()
                .map(activityMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<Activity> getAllActivityEntities() {
        return activityRepository.findAllWithoutAssociations();
    }

    @Override
    @Cacheable(value = CacheNames.ACTIVITIES_BY_CATEGORY, key = "#categoryId")
    public List<ActivityResponseDto> getActivitiesByCategory(int categoryId) {
        return activityRepository.findAllByActivityCategory_Id(categoryId).stream()
                .map(activityMapper::toResponseDto)
                .toList();
    }

    @Override
    public ActivityAverageMetResponseDto getAverageMet() {
        return ActivityAverageMetResponseDto.of(activityRepository.findAll().stream()
                .mapToDouble(Activity::getMet)
                .average()
                .orElse(0.0)
        );
    }

    private ActivityUpdateDto applyPatchToActivity(Activity activity, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        ActivityResponseDto responseDto = activityMapper.toResponseDto(activity);
        return jsonPatchService.applyPatch(patch, responseDto, ActivityUpdateDto.class);
    }

    private void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

    private Activity findActivity(int activityId) {
        return repositoryHelper.find(activityRepository, Activity.class, activityId);
    }

    private User findUser(int userId) {
        return repositoryHelper.find(userRepository, User.class, userId);
    }
}