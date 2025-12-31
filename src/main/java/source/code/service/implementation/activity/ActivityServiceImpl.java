package source.code.service.implementation.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.activity.ActivityPopulationService;
import source.code.service.declaration.activity.ActivityService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ActivitySpecification;

@Service
public class ActivityServiceImpl implements ActivityService {

	private final RepositoryHelper repositoryHelper;

	private final ActivityMapper activityMapper;

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final ApplicationEventPublisher eventPublisher;

	private final ActivityRepository activityRepository;

	private final ActivityPopulationService activityPopulationService;

	private final UserRepository userRepository;

	private final SpecificationDependencies dependencies;

	public ActivityServiceImpl(RepositoryHelper repositoryHelper, ActivityMapper activityMapper,
			ValidationService validationService, JsonPatchService jsonPatchService,
			ApplicationEventPublisher eventPublisher, ActivityRepository activityRepository,
			ActivityPopulationService activityPopulationService, UserRepository userRepository,
			SpecificationDependencies dependencies) {
		this.repositoryHelper = repositoryHelper;
		this.activityMapper = activityMapper;
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.eventPublisher = eventPublisher;
		this.activityRepository = activityRepository;
		this.activityPopulationService = activityPopulationService;
		this.userRepository = userRepository;
		this.dependencies = dependencies;
	}

	@Override
	@Transactional
	public ActivityResponseDto createActivity(ActivityCreateDto dto) {
		Activity saved = activityRepository.save(activityMapper.toEntity(dto));

		activityRepository.flush();

		Activity activity = activityRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Activity.class, saved.getId()));

		eventPublisher.publishEvent(ActivityCreateEvent.of(this, activity));

		var responseDto = activityMapper.toDetailedResponseDto(activity);
		activityPopulationService.populate(responseDto);

		return responseDto;
	}

	@Override
	@Transactional
	public void updateActivity(int activityId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		Activity activity = findActivity(activityId);
		ActivityUpdateDto patched = applyPatchToActivity(patch);

		validationService.validate(patched);
		activityMapper.updateActivityFromDto(activity, patched);
		Activity saved = activityRepository.save(activity);

		activityRepository.flush();

		Activity refetchedActivity = activityRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Activity.class, saved.getId()));

		eventPublisher.publishEvent(ActivityUpdateEvent.of(this, refetchedActivity));
	}

	@Override
	@Transactional
	public void deleteActivity(int activityId) {
		Activity activity = findActivityWithAssociations(activityId);
		activityRepository.delete(activity);

		eventPublisher.publishEvent(ActivityDeleteEvent.of(this, activity));
	}

	@Override
	public ActivityCalculatedResponseDto calculateCaloriesBurned(int activityId,
			CalculateActivityCaloriesRequestDto request) {
		Activity activity = findActivity(activityId);
		BigDecimal weight = resolveWeightForCalculation(request);

		return activityMapper.toCalculatedDto(activity, weight, request.getTime());
	}

	@Override
	@Cacheable(value = CacheNames.ACTIVITIES, key = "#activityId")
	public ActivityResponseDto getActivity(int activityId) {
		return findAndMap(activityId);
	}

	@Override
	public Page<ActivitySummaryDto> getFilteredActivities(FilterDto filter, Pageable pageable) {
		SpecificationFactory<Activity> activityFactory = ActivitySpecification::new;
		SpecificationBuilder<Activity> specificationBuilder = SpecificationBuilder.of(filter, activityFactory,
				dependencies);
		Specification<Activity> specification = specificationBuilder.build();

		Page<Activity> activityPage = activityRepository.findAll(specification, pageable);

		List<ActivitySummaryDto> summaries = activityPage.getContent()
			.stream()
			.map(activityMapper::toSummaryDto)
			.toList();

		activityPopulationService.populate(summaries);

		return new PageImpl<>(summaries, pageable, activityPage.getTotalElements());
	}

	@Override
	public List<Activity> getAllActivityEntities() {
		return activityRepository.findAll();
	}

	private ActivityUpdateDto applyPatchToActivity(JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class);
	}

	private Activity findActivity(int activityId) {
		return repositoryHelper.find(activityRepository, Activity.class, activityId);
	}

	private ActivityResponseDto findAndMap(int activityId) {
		Activity activity = activityRepository.findByIdWithMedia(activityId)
			.orElseThrow(() -> RecordNotFoundException.of(Activity.class, activityId));
		ActivityResponseDto dto = activityMapper.toDetailedResponseDto(activity);
		activityPopulationService.populate(dto);
		return dto;
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

		throw new WeightRequiredException("Weight is required for calorie calculation. "
				+ "Please provide it in the request or set it in your profile.");
	}

}
