package source.code.service.implementation.food;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.helper.utils.AuthorizationUtil;
import source.code.repository.UserFoodRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.food.FoodPopulationService;
import source.code.service.declaration.helpers.ImageUrlPopulationService;

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
		if (foods.isEmpty())
			return;

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
			.collect(Collectors.toMap(SavesProjection::getEntityId, projection -> projection));

		foods.forEach(food -> {
			SavesProjection counts = countsMap.get(food.getId());
			if (counts != null) {
				food.setSavesCount(counts.savesCount());
				food.setSaved(counts.isSaved());
			}
		});
	}

}
