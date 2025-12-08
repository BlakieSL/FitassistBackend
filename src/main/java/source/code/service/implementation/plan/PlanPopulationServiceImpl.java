package source.code.service.implementation.plan;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.plan.PlanCountsProjection;
import source.code.dto.pojo.projection.recipe.RecipeAndPlanUserInteractionProjection;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.repository.UserPlanRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.plan.PlanPopulationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlanPopulationServiceImpl implements PlanPopulationService {
    private final UserPlanRepository userPlanRepository;
    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;

    public PlanPopulationServiceImpl(
            UserPlanRepository userPlanRepository,
            MediaRepository mediaRepository,
            AwsS3Service s3Service) {
        this.userPlanRepository = userPlanRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void populate(List<PlanSummaryDto> plans) {
        if (plans.isEmpty()) return;

        List<Integer> planIds = plans.stream()
                .map(PlanSummaryDto::getId)
                .toList();

        populateAuthorImages(plans);
        populateImageUrls(plans);
        populateCounts(plans, planIds);
    }

    @Override
    public void populate(PlanResponseDto dto) {
        int userId = AuthorizationUtil.getUserId();

        populateAuthorImage(dto);
        populateImageUrls(dto);
        populateUserInteractionsAndCounts(dto, userId);
    }

    private void populateAuthorImages(List<PlanSummaryDto> plans) {
        List<Integer> authorIds = plans.stream()
                .map(PlanSummaryDto::getAuthorId)
                .toList();

        if (authorIds.isEmpty()) return;

        Map<Integer, String> authorImageMap = mediaRepository
                .findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
                .stream()
                .collect(Collectors.toMap(Media::getParentId, Media::getImageName));

        plans.forEach(plan -> {
            String imageName = authorImageMap.get(plan.getAuthorId());
            if (imageName != null) {
                plan.setAuthorImageName(imageName);
                plan.setAuthorImageUrl(s3Service.getImage(imageName));
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

    private void populateCounts(List<PlanSummaryDto> plans, List<Integer> planIds) {
        Map<Integer, PlanCountsProjection> countsMap = userPlanRepository
                .findCountsByPlanIds(planIds)
                .stream()
                .collect(Collectors.toMap(
                        PlanCountsProjection::getPlanId,
                        projection -> projection
                ));

        plans.forEach(plan -> {
            PlanCountsProjection counts = countsMap.get(plan.getId());
            if (counts != null) {
                plan.setLikesCount(counts.getLikesCount());
                plan.setDislikesCount(counts.getDislikesCount());
                plan.setSavesCount(counts.getSavesCount());
            }
        });
    }

    private void populateAuthorImage(PlanResponseDto plan) {
        mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(plan.getAuthorId(), MediaConnectedEntity.USER)
                .ifPresent(media -> {
                    plan.setAuthorImageName(media.getImageName());
                    plan.setAuthorImageUrl(s3Service.getImage(media.getImageName()));
                });
    }

    private void populateImageUrls(PlanResponseDto plan) {
        List<String> imageUrls = mediaRepository.findByParentIdAndParentType(plan.getId(), MediaConnectedEntity.PLAN)
                .stream()
                .map(media -> s3Service.getImage(media.getImageName()))
                .toList();
        plan.setImageUrls(imageUrls);
    }

    private void populateUserInteractionsAndCounts(PlanResponseDto plan, int requestingUserId) {
        RecipeAndPlanUserInteractionProjection result = userPlanRepository
                .findUserInteractionsAndCounts(requestingUserId, plan.getId());

        if (result == null) return;

        plan.setLiked(result.isLiked());
        plan.setDisliked(result.isDisliked());
        plan.setSaved(result.isSaved());
        plan.setLikesCount(result.likesCount());
        plan.setDislikesCount(result.dislikesCount());
        plan.setSavesCount(result.savesCount());
    }
}