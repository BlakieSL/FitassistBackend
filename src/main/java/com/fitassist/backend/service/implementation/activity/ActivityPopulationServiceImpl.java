package com.fitassist.backend.service.implementation.activity;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.projection.SavesProjection;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.repository.UserActivityRepository;
import com.fitassist.backend.service.declaration.activity.ActivityPopulationService;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.helpers.ImageUrlPopulationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ActivityPopulationServiceImpl implements ActivityPopulationService {

	private final UserActivityRepository userActivityRepository;

	private final AwsS3Service s3Service;

	private final ImageUrlPopulationService imageUrlPopulationService;

	public ActivityPopulationServiceImpl(UserActivityRepository userActivityRepository, AwsS3Service s3Service,
			ImageUrlPopulationService imageUrlPopulationService) {
		this.userActivityRepository = userActivityRepository;
		this.s3Service = s3Service;
		this.imageUrlPopulationService = imageUrlPopulationService;
	}

	@Override
	public void populate(ActivityResponseDto activity) {
		int userId = AuthorizationUtil.getUserId();

		SavesProjection savesData = userActivityRepository.findSavesCountAndUserSaved(activity.getId(), userId);
		activity.setSavesCount(savesData.savesCount());
		activity.setSaved(savesData.isSaved());

		imageUrlPopulationService.populateImageUrls(activity.getImages());
	}

	@Override
	public void populate(List<ActivitySummaryDto> activities) {
		if (activities.isEmpty()) {
			return;
		}

		List<Integer> activityIds = activities.stream().map(ActivitySummaryDto::getId).toList();

		populateImageUrls(activities);
		fetchAndPopulateUserInteractionsAndCounts(activities, activityIds);
	}

	private void populateImageUrls(List<ActivitySummaryDto> activities) {
		activities.forEach(activity -> {
			if (activity.getImageName() != null) {
				activity.setFirstImageUrl(s3Service.getImage(activity.getImageName()));
			}
		});
	}

	private void fetchAndPopulateUserInteractionsAndCounts(List<ActivitySummaryDto> activities,
			List<Integer> activityIds) {
		int userId = AuthorizationUtil.getUserId();

		Map<Integer, SavesProjection> countsMap = userActivityRepository
			.findCountsAndInteractionsByActivityIds(userId, activityIds)
			.stream()
			.collect(Collectors.toMap(SavesProjection::getEntityId, Function.identity()));

		activities.forEach(activity -> {
			SavesProjection counts = countsMap.get(activity.getId());
			if (counts != null) {
				activity.setSavesCount(counts.savesCount());
				activity.setSaved(counts.isSaved());
			}
		});
	}

}
