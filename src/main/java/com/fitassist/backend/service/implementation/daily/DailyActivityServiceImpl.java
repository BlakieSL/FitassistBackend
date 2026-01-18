package com.fitassist.backend.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.activity.DailyActivityItemCreateDto;
import com.fitassist.backend.dto.request.activity.DailyActivityItemUpdateDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.daily.DailyActivitiesResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.exception.WeightRequiredException;
import com.fitassist.backend.mapper.daily.DailyActivityMapper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.daily.DailyCart;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.ActivityRepository;
import com.fitassist.backend.repository.DailyCartActivityRepository;
import com.fitassist.backend.repository.DailyCartRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.daily.DailyActivityService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class DailyActivityServiceImpl implements DailyActivityService {

	private final JsonPatchService jsonPatchService;

	private final ValidationService validationService;

	private final DailyActivityMapper dailyActivityMapper;

	private final RepositoryHelper repositoryHelper;

	private final DailyCartRepository dailyCartRepository;

	private final DailyCartActivityRepository dailyCartActivityRepository;

	private final ActivityRepository activityRepository;

	private final UserRepository userRepository;

	public DailyActivityServiceImpl(DailyCartRepository dailyCartRepository, UserRepository userRepository,
			ActivityRepository activityRepository, JsonPatchService jsonPatchService,
			ValidationService validationService, DailyActivityMapper dailyActivityMapper,
			RepositoryHelper repositoryHelper, DailyCartActivityRepository dailyCartActivityRepository) {
		this.dailyCartRepository = dailyCartRepository;
		this.userRepository = userRepository;
		this.activityRepository = activityRepository;
		this.jsonPatchService = jsonPatchService;
		this.validationService = validationService;
		this.dailyActivityMapper = dailyActivityMapper;
		this.repositoryHelper = repositoryHelper;
		this.dailyCartActivityRepository = dailyCartActivityRepository;
	}

	@Override
	@Transactional
	public void addActivityToDailyCart(int activityId, DailyActivityItemCreateDto dto) {
		int userId = AuthorizationUtil.getUserId();
		DailyCart dailyCart = getOrCreateDailyCartForUser(userId, dto.getDate());
		Activity activity = repositoryHelper.find(activityRepository, Activity.class, activityId);

		BigDecimal weight = resolveWeightForLogging(dto.getWeight(), userId);
		updateOrAddDailyActivityItem(dailyCart, activity, dto.getTime(), weight);
		dailyCartRepository.save(dailyCart);
	}

	@Override
	@Transactional
	public void removeActivityFromDailyCart(int dailyActivityItemId) {
		DailyCartActivity dailyCartActivity = findWithoutAssociations(dailyActivityItemId);
		dailyCartActivityRepository.delete(dailyCartActivity);
	}

	@Override
	@Transactional
	public void updateDailyActivityItem(int dailyActivityItemId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		DailyCartActivity dailyCartActivity = findWithoutAssociations(dailyActivityItemId);
		DailyActivityItemUpdateDto patchedDto = applyPatchToDailyActivityItem(patch);
		validationService.validate(patchedDto);

		updateTime(dailyCartActivity, patchedDto.getTime());
		updateWeight(dailyCartActivity, patchedDto.getWeight());

		dailyCartActivityRepository.save(dailyCartActivity);
	}

	@Override
	public DailyActivitiesResponseDto getActivitiesFromDailyCart(LocalDate date) {
		int userId = AuthorizationUtil.getUserId();

		return dailyCartRepository.findByUserIdAndDateWithActivityAssociations(userId, date)
			.map(dailyCart -> dailyCart.getDailyCartActivities()
				.stream()
				.map(dailyActivityMapper::toActivityCalculatedResponseDto)
				.collect(Collectors.teeing(Collectors.toList(),
						Collectors.reducing(BigDecimal.ZERO, ActivityCalculatedResponseDto::getCaloriesBurned,
								BigDecimal::add),
						DailyActivitiesResponseDto::of)))
			.orElse(DailyActivitiesResponseDto.of(Collections.emptyList(), BigDecimal.ZERO));
	}

	private void updateOrAddDailyActivityItem(DailyCart dailyCart, Activity activity, Short time, BigDecimal weight) {
		dailyCartActivityRepository.findByDailyCartIdAndActivityId(dailyCart.getId(), activity.getId())
			.ifPresentOrElse(foundItem -> {
				foundItem.setTime((short) (foundItem.getTime() + time));
				foundItem.setWeight(weight);
			}, () -> {
				DailyCartActivity newItem = DailyCartActivity.of(activity, dailyCart, time, weight);
				dailyCart.getDailyCartActivities().add(newItem);
			});
	}

	private void updateTime(DailyCartActivity dailyCartActivity, Short time) {
		dailyCartActivity.setTime(time);
	}

	private void updateWeight(DailyCartActivity dailyCartActivity, BigDecimal weight) {
		if (weight != null) {
			int userId = AuthorizationUtil.getUserId();
			BigDecimal resolvedWeight = resolveWeightForLogging(weight, userId);
			dailyCartActivity.setWeight(resolvedWeight);
		}
	}

	private DailyCart createDailyCart(int userId, LocalDate date) {
		User user = repositoryHelper.find(userRepository, User.class, userId);
		return dailyCartRepository.save(DailyCart.of(user, date));
	}

	private DailyActivityItemUpdateDto applyPatchToDailyActivityItem(JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, DailyActivityItemUpdateDto.class);
	}

	private DailyCart getOrCreateDailyCartForUser(int userId, LocalDate date) {
		return dailyCartRepository.findByUserIdAndDate(userId, date).orElseGet(() -> createDailyCart(userId, date));
	}

	private DailyCartActivity findWithoutAssociations(int dailyCartActivityId) {
		return dailyCartActivityRepository.findByIdWithoutAssociations(dailyCartActivityId)
			.orElseThrow(() -> new RecordNotFoundException(DailyCartActivity.class, dailyCartActivityId));
	}

	private BigDecimal resolveWeightForLogging(BigDecimal requestWeight, int userId) {
		if (requestWeight != null) {
			return requestWeight;
		}

		User user = repositoryHelper.find(userRepository, User.class, userId);

		if (user.getWeight() != null) {
			return user.getWeight();
		}

		throw new WeightRequiredException("Weight is required for logging activities. "
				+ "Please provide it in the request or set it in your profile.");
	}

}
