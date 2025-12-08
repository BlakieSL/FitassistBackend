package source.code.service.implementation.exercise;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.MediaRepository;
import source.code.repository.UserExerciseRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.exercise.ExercisePopulationService;

import java.util.List;

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

        populateUserInteractionsAndCounts(exercise, userId);
        populateImageUrls(exercise);
    }

    private void populateUserInteractionsAndCounts(ExerciseResponseDto exercise, int userId) {
        SavesProjection savesData = userExerciseRepository.findSavesCountAndUserSaved(exercise.getId(), userId);
        exercise.setSavesCount(savesData.savesCount());
        exercise.setSaved(savesData.isSaved());
    }

    private void populateImageUrls(ExerciseResponseDto exercise) {
        List<String> imageUrls = mediaRepository.findByParentIdAndParentType(exercise.getId(), MediaConnectedEntity.EXERCISE)
                .stream()
                .map(media -> awsS3Service.getImage(media.getImageName()))
                .toList();
        exercise.setImageUrls(imageUrls);
    }
}
