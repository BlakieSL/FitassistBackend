package source.code.service.implementation.plan;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.EntityCountsProjection;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.utils.AuthorizationUtil;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.repository.UserPlanRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.plan.PlanPopulationService;

@Service
public class PlanPopulationServiceImpl implements PlanPopulationService {

	private final UserPlanRepository userPlanRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public PlanPopulationServiceImpl(UserPlanRepository userPlanRepository, MediaRepository mediaRepository,
									 AwsS3Service s3Service) {
		this.userPlanRepository = userPlanRepository;
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
	}

	@Override
	public void populate(List<PlanSummaryDto> plans) {
		if (plans.isEmpty())
			return;

		List<Integer> planIds = plans.stream().map(PlanSummaryDto::getId).toList();

		fetchAndPopulateAuthorImages(plans);
		populateImageUrls(plans);
		fetchAndPopulateUserInteractionsAndCounts(plans, planIds);
	}

	@Override
	public void populate(PlanResponseDto dto) {
		int userId = AuthorizationUtil.getUserId();

		fetchAndPopulateAuthorImage(dto);
		fetchAndPopulateImageUrls(dto);
		fetchAndPopulateUserInteractionsAndCounts(dto, userId);
	}

	private void fetchAndPopulateAuthorImages(List<PlanSummaryDto> plans) {
		List<Integer> authorIds = plans.stream().map(plan -> plan.getAuthor().getId()).toList();

		if (authorIds.isEmpty())
			return;

		Map<Integer, String> authorImageMap = mediaRepository
			.findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
			.stream()
			.collect(Collectors.toMap(Media::getParentId, Media::getImageName));

		plans.forEach(plan -> {
			String imageName = authorImageMap.get(plan.getAuthor().getId());
			if (imageName != null) {
				plan.getAuthor().setImageName(imageName);
				plan.getAuthor().setImageUrl(s3Service.getImage(imageName));
			}
		});
	}

	private void populateImageUrls(List<PlanSummaryDto> plans) {
		plans.forEach(plan -> {
			if (plan.getFirstImageName() != null) {
				plan.setFirstImageUrl(s3Service.getImage(plan.getFirstImageName()));
			}
		});
	}

	private void fetchAndPopulateUserInteractionsAndCounts(List<PlanSummaryDto> plans, List<Integer> planIds) {
		int userId = AuthorizationUtil.getUserId();

		Map<Integer, EntityCountsProjection> countsMap = userPlanRepository
			.findCountsAndInteractionsByPlanIds(userId, planIds)
			.stream()
			.collect(Collectors.toMap(EntityCountsProjection::getEntityId, projection -> projection));

		plans.forEach(plan -> {
			EntityCountsProjection counts = countsMap.get(plan.getId());
			if (counts != null) {
				plan.setLikesCount(counts.likesCount());
				plan.setDislikesCount(counts.dislikesCount());
				plan.setSavesCount(counts.savesCount());
				plan.setLiked(counts.isLiked());
				plan.setDisliked(counts.isDisliked());
				plan.setSaved(counts.isSaved());
			}
		});
	}

	private void fetchAndPopulateAuthorImage(PlanResponseDto plan) {
		mediaRepository
			.findFirstByParentIdAndParentTypeOrderByIdAsc(plan.getAuthor().getId(), MediaConnectedEntity.USER)
			.ifPresent(media -> {
				plan.getAuthor().setImageName(media.getImageName());
				plan.getAuthor().setImageUrl(s3Service.getImage(media.getImageName()));
			});
	}

	private void fetchAndPopulateImageUrls(PlanResponseDto plan) {
		List<String> imageUrls = mediaRepository.findByParentIdAndParentType(plan.getId(), MediaConnectedEntity.PLAN)
			.stream()
			.map(media -> s3Service.getImage(media.getImageName()))
			.toList();
		plan.setImageUrls(imageUrls);
	}

	private void fetchAndPopulateUserInteractionsAndCounts(PlanResponseDto plan, int requestingUserId) {
		EntityCountsProjection result = userPlanRepository.findCountsAndInteractionsByPlanId(requestingUserId,
			plan.getId());

		if (result == null)
			return;

		plan.setLiked(result.isLiked());
		plan.setDisliked(result.isDisliked());
		plan.setSaved(result.isSaved());
		plan.setLikesCount(result.likesCount());
		plan.setDislikesCount(result.dislikesCount());
		plan.setSavesCount(result.savesCount());
	}

}
