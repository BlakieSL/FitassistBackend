package source.code.service.implementation.activity;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserActivityRepository;
import source.code.service.declaration.activity.ActivityPopulationService;
import source.code.service.declaration.aws.AwsS3Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityPopulationServiceImpl implements ActivityPopulationService {
    private final UserActivityRepository userActivityRepository;
    private final AwsS3Service s3Service;

    public ActivityPopulationServiceImpl(UserActivityRepository userActivityRepository, AwsS3Service s3Service) {
        this.userActivityRepository = userActivityRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void populate(ActivityResponseDto activity) {
        int userId = AuthorizationUtil.getUserId();

        SavesProjection savesData = userActivityRepository.findSavesCountAndUserSaved(activity.getId(), userId);
        activity.setSavesCount(savesData.savesCount());
        activity.setSaved(savesData.isSaved());
    }

    @Override
    public void populate(List<ActivitySummaryDto> activities) {
        if (activities.isEmpty()) return;

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

    private void fetchAndPopulateUserInteractionsAndCounts(List<ActivitySummaryDto> activities, List<Integer> activityIds) {
        int userId = AuthorizationUtil.getUserId();

        Map<Integer, SavesProjection> countsMap = userActivityRepository
                .findCountsAndInteractionsByActivityIds(userId, activityIds)
                .stream()
                .collect(Collectors.toMap(
                        SavesProjection::getEntityId,
                        projection -> projection
                ));

        activities.forEach(activity -> {
            SavesProjection counts = countsMap.get(activity.getId());
            if (counts != null) {
                activity.setSavesCount(counts.savesCount());
                activity.setSaved(counts.isSaved());
            }
        });
    }
}
