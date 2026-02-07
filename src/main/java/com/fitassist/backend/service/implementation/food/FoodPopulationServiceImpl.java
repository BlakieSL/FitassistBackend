package com.fitassist.backend.service.implementation.food;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.projection.SavesProjection;
import com.fitassist.backend.dto.response.food.FoodResponseDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.repository.UserFoodRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.food.FoodPopulationService;
import com.fitassist.backend.service.declaration.helpers.ImageUrlPopulationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FoodPopulationServiceImpl implements FoodPopulationService {

	private final UserFoodRepository userFoodRepository;

	private final AwsS3Service s3Service;

	private final ImageUrlPopulationService imageUrlPopulationService;

	public FoodPopulationServiceImpl(UserFoodRepository userFoodRepository, AwsS3Service s3Service,
			ImageUrlPopulationService imageUrlPopulationService) {
		this.userFoodRepository = userFoodRepository;
		this.s3Service = s3Service;
		this.imageUrlPopulationService = imageUrlPopulationService;
	}

	@Override
	public void populate(FoodResponseDto food) {
		int userId = AuthorizationUtil.getUserId();

		SavesProjection savesData = userFoodRepository.findCountsAndInteractionsByFoodId(food.getId(), userId);
		food.setSavesCount(savesData.savesCount());
		food.setSaved(savesData.isSaved());

		imageUrlPopulationService.populateImageUrls(food.getImages());
	}

	@Override
	public void populate(List<FoodSummaryDto> foods) {
		if (foods.isEmpty()) {
			return;
		}

		List<Integer> foodIds = foods.stream().map(FoodSummaryDto::getId).toList();

		populateImageUrls(foods);
		fetchAndPopulateUserInteractionsAndCounts(foods, foodIds);
	}

	private void populateImageUrls(List<FoodSummaryDto> foods) {
		foods.forEach(food -> {
			if (food.getImageName() != null) {
				food.setFirstImageUrl(s3Service.getImage(food.getImageName()));
			}
		});
	}

	private void fetchAndPopulateUserInteractionsAndCounts(List<FoodSummaryDto> foods, List<Integer> foodIds) {
		int userId = AuthorizationUtil.getUserId();

		Map<Integer, SavesProjection> countsMap = userFoodRepository.findCountsAndInteractionsByFoodIds(userId, foodIds)
			.stream()
			.collect(Collectors.toMap(SavesProjection::getEntityId, Function.identity()));

		foods.forEach(food -> {
			SavesProjection counts = countsMap.get(food.getId());
			if (counts != null) {
				food.setSavesCount(counts.savesCount());
				food.setSaved(counts.isSaved());
			}
		});
	}

}
