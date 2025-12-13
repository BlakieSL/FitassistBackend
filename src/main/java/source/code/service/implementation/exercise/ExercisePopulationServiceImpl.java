package source.code.service.implementation.exercise;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.MediaRepository;
import source.code.repository.UserExerciseRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.exercise.ExercisePopulationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExercisePopulationServiceImpl implements ExercisePopulationService {
    private final UserExerciseRepository userExerciseRepository;
    private final MediaRepository mediaRepository;
    private final AwsS3Service awsS3Service;

    public ExercisePopulationServiceImpl(UserExerciseRepository userExerciseRepository, MediaRepository mediaRepository, AwsS3Service awsS3Service) {
        this.userExerciseRepository = userExerciseRepository;
        this.mediaRepository = mediaRepository;
        this.awsS3Service = awsS3Service;
    }

    @Override
    public void populate(ExerciseResponseDto exercise) {
        int userId = AuthorizationUtil.getUserId();

        fetchAndPopulateUserInteractionsAndCounts(exercise, userId);
        fetchAndPopulateImageUrls(exercise);
    }

    @Override
    public void populate(List<ExerciseSummaryDto> exercises) {
        if (exercises.isEmpty()) return;

        List<Integer> exerciseIds = exercises.stream().map(ExerciseSummaryDto::getId).toList();

        populateImageUrls(exercises);
        fetchAndPopulateUserInteractionsAndCounts(exercises, exerciseIds);
    }

    private void populateImageUrls(List<ExerciseSummaryDto> exercises) {
        exercises.forEach(exercise -> {
            if (exercise.getImageName() != null) {
                exercise.setFirstImageUrl(awsS3Service.getImage(exercise.getImageName()));
            }
        });
    }

    private void fetchAndPopulateUserInteractionsAndCounts(List<ExerciseSummaryDto> exercises, List<Integer> exerciseIds) {
        int userId = AuthorizationUtil.getUserId();

        Map<Integer, SavesProjection> countsMap = userExerciseRepository
                .findCountsAndInteractionsByExerciseIds(userId, exerciseIds)
                .stream()
                .collect(Collectors.toMap(
                        SavesProjection::getEntityId,
                        projection -> projection
                ));

        exercises.forEach(exercise -> {
            SavesProjection counts = countsMap.get(exercise.getId());
            if (counts != null) {
                exercise.setSavesCount(counts.savesCount());
                exercise.setSaved(counts.isSaved());
            }
        });
    }

    private void fetchAndPopulateUserInteractionsAndCounts(ExerciseResponseDto exercise, int userId) {
        SavesProjection savesData = userExerciseRepository.findCountsAndInteractions(exercise.getId(), userId);
        exercise.setSavesCount(savesData.savesCount());
        exercise.setSaved(savesData.isSaved());
    }

    private void fetchAndPopulateImageUrls(ExerciseResponseDto exercise) {
        List<String> imageUrls = mediaRepository.findByParentIdAndParentType(exercise.getId(), MediaConnectedEntity.EXERCISE)
                .stream()
                .map(media -> awsS3Service.getImage(media.getImageName()))
                .toList();
        exercise.setImageUrls(imageUrls);
    }
}
