package source.code.service.implementation.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.ActivityUpdateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.event.events.Activity.ActivityCreateEvent;
import source.code.event.events.Activity.ActivityDeleteEvent;
import source.code.event.events.Activity.ActivityUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.exception.WeightRequiredException;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.activity.ActivityService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ActivitySpecification;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final RepositoryHelper repositoryHelper;
    private final ActivityMapper activityMapper;
    private final ValidationService validationService;
    private final JsonPatchService jsonPatchService;
    private final ApplicationEventPublisher eventPublisher;
    private final ActivityRepository activityRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;
    private final SpecificationDependencies dependencies;

    public ActivityServiceImpl(
            RepositoryHelper repositoryHelper,
            ActivityMapper activityMapper,
            ValidationService validationService,
            JsonPatchService jsonPatchService,
            ApplicationEventPublisher eventPublisher,
            ActivityRepository activityRepository,
            UserActivityRepository userActivityRepository,
            UserRepository userRepository,
            SpecificationDependencies dependencies) {
        this.repositoryHelper = repositoryHelper;
        this.activityMapper = activityMapper;
        this.validationService = validationService;
        this.jsonPatchService = jsonPatchService;
        this.eventPublisher = eventPublisher;
        this.activityRepository = activityRepository;
        this.userActivityRepository = userActivityRepository;
        this.userRepository = userRepository;
        this.dependencies = dependencies;
    }

    @Override
    @Transactional
    public ActivitySummaryDto createActivity(ActivityCreateDto dto) {
        Activity activity = activityRepository.save(activityMapper.toEntity(dto));
        eventPublisher.publishEvent(ActivityCreateEvent.of(this, activity));

        return activityMapper.toSummaryDto(activity);
    }

    @Override
    @Transactional
    public void updateActivity(int activityId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Activity activity = findActivity(activityId);
        ActivityUpdateDto patched = applyPatchToActivity(patch);

        validationService.validate(patched);
        activityMapper.updateActivityFromDto(activity, patched);
        Activity savedActivity = activityRepository.save(activity);

        eventPublisher.publishEvent(ActivityUpdateEvent.of(this, savedActivity));
    }

    @Override
    @Transactional
    public void deleteActivity(int activityId) {
        Activity activity = findActivityWithAssociations(activityId);
        activityRepository.delete(activity);

        eventPublisher.publishEvent(ActivityDeleteEvent.of(this, activity));
    }

    @Override
    public ActivityCalculatedResponseDto calculateCaloriesBurned(
            int activityId, CalculateActivityCaloriesRequestDto request
    ) {
        Activity activity = findActivity(activityId);

        BigDecimal weight = resolveWeightForCalculation(request);

        return activityMapper.toCalculatedDto(activity, weight, request.getTime());
    }

    @Override
    @Cacheable(value = CacheNames.ACTIVITIES, key = "#activityId")
    public ActivityResponseDto getActivity(int activityId) {
        Activity activity = activityRepository.findByIdWithMedia(activityId)
                .orElseThrow(() -> RecordNotFoundException.of(Activity.class, activityId));

        ActivityResponseDto dto = activityMapper.toDetailedResponseDto(activity);
        int userId = AuthorizationUtil.getUserId();
        dto.setSavesCount(userActivityRepository.countByActivityId(activityId));
        dto.setSaved(userActivityRepository.existsByUserIdAndActivityId(userId, activityId));

        return dto;
    }

    @Override
    public Page<ActivitySummaryDto> getAllActivities(Pageable pageable) {
        return activityRepository.findAllWithActivityCategory(pageable)
                .map(activityMapper::toSummaryDto);
    }

    @Override
    public Page<ActivitySummaryDto> getFilteredActivities(FilterDto filter, Pageable pageable) {
        SpecificationFactory<Activity> activityFactory = ActivitySpecification::of;
        SpecificationBuilder<Activity> specificationBuilder = SpecificationBuilder.of(filter, activityFactory, dependencies);
        Specification<Activity> specification = specificationBuilder.build();

        return activityRepository.findAll(specification, pageable)
                .map(activityMapper::toSummaryDto);
    }

    @Override
    public List<Activity> getAllActivityEntities() {
        return activityRepository.findAllWithoutAssociations();
    }

    private ActivityUpdateDto applyPatchToActivity(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        return jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class);
    }

    private Activity findActivity(int activityId) {
        return repositoryHelper.find(activityRepository, Activity.class, activityId);
    }

    private Activity findActivityWithAssociations(int activityId) {
        return activityRepository.findByIdWithAssociations(activityId)
                .orElseThrow(() -> new RecordNotFoundException(Activity.class, activityId));
    }

    private User findUser(int userId) {
        return repositoryHelper.find(userRepository, User.class, userId);
    }


    private BigDecimal resolveWeightForCalculation(CalculateActivityCaloriesRequestDto request) {
        if (request.getWeight() != null) {
            return request.getWeight();
        }

        int userId = AuthorizationUtil.getUserId();
        User user = findUser(userId);

        if (user.getWeight() != null) {
            return user.getWeight();
        }

        throw new WeightRequiredException(
                "Weight is required for calorie calculation. " +
                        "Please provide it in the request or set it in your profile."
        );
    }
}