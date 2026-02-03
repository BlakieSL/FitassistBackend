package com.fitassist.backend.service.implementation.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.ActivityUpdateDto;
import com.fitassist.backend.dto.request.activity.CalculateActivityCaloriesRequestDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.event.event.Activity.ActivityCreateEvent;
import com.fitassist.backend.event.event.Activity.ActivityDeleteEvent;
import com.fitassist.backend.event.event.Activity.ActivityUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.exception.WeightRequiredException;
import com.fitassist.backend.mapper.activity.ActivityMapper;
import com.fitassist.backend.mapper.activity.ActivityMappingContext;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.activity.ActivityCategory;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.ActivityCategoryRepository;
import com.fitassist.backend.repository.ActivityRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.activity.ActivityPopulationService;
import com.fitassist.backend.service.declaration.activity.ActivityService;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.ActivitySpecification;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private final ActivityCategoryRepository activityCategoryRepository;

	private final ActivityPopulationService activityPopulationService;

	private final UserRepository userRepository;

	private final SpecificationDependencies dependencies;
	private final CalculationsService calculationsService;

	public ActivityServiceImpl(RepositoryHelper repositoryHelper, ActivityMapper activityMapper,
							   ValidationService validationService, JsonPatchService jsonPatchService,
							   ApplicationEventPublisher eventPublisher, ActivityRepository activityRepository,
							   ActivityCategoryRepository activityCategoryRepository, ActivityPopulationService activityPopulationService,
							   UserRepository userRepository, SpecificationDependencies dependencies, CalculationsService calculationsService) {
		this.repositoryHelper = repositoryHelper;
		this.activityMapper = activityMapper;
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.eventPublisher = eventPublisher;
		this.activityRepository = activityRepository;
		this.activityCategoryRepository = activityCategoryRepository;
		this.activityPopulationService = activityPopulationService;
		this.userRepository = userRepository;
		this.dependencies = dependencies;
		this.calculationsService = calculationsService;
	}

	@Override
	@Transactional
	public ActivityResponseDto createActivity(ActivityCreateDto dto) {
		ActivityMappingContext context = prepareCreateContext(dto);
		Activity saved = activityRepository.save(activityMapper.toEntity(dto, context));

		activityRepository.flush();

		Activity activity = activityRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Activity.class, saved.getId()));

		eventPublisher.publishEvent(ActivityCreateEvent.of(this, activity));

		var responseDto = activityMapper.toDetailedResponseDto(activity);
		activityPopulationService.populate(responseDto);

		return responseDto;
	}

	private ActivityMappingContext prepareCreateContext(ActivityCreateDto dto) {
		ActivityCategory category = findCategory(dto.getCategoryId());
		return new ActivityMappingContext(category);
	}

	private ActivityCategory findCategory(Integer categoryId) {
		if (categoryId == null) {
			return null;
		}
		return activityCategoryRepository.findById(categoryId)
			.orElseThrow(() -> RecordNotFoundException.of(ActivityCategory.class, categoryId));
	}

	@Override
	@Transactional
	public void updateActivity(int activityId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		Activity activity = findActivity(activityId);
		ActivityUpdateDto patched = applyPatchToActivity(patch);
		validationService.validate(patched);

		ActivityMappingContext context = prepareUpdateContext(patched);
		activityMapper.updateActivityFromDto(activity, patched, context);

		Activity saved = activityRepository.save(activity);

		activityRepository.flush();

		Activity refetchedActivity = activityRepository.findByIdWithMedia(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Activity.class, saved.getId()));

		eventPublisher.publishEvent(ActivityUpdateEvent.of(this, refetchedActivity));
	}

	private ActivityMappingContext prepareUpdateContext(ActivityUpdateDto dto) {
		ActivityCategory category = findCategory(dto.getCategoryId());
		return new ActivityMappingContext(category);
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

		return calculationsService.toCalculatedResponseDto(activity, weight, request.getTime());
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
