package com.fitassist.backend.service.implementation.exercise;

import org.springframework.stereotype.Service;
import com.fitassist.backend.dto.pojo.projection.SavesProjection;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.repository.UserExerciseRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.exercise.ExercisePopulationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExercisePopulationServiceImpl implements ExercisePopulationService {

	private final UserExerciseRepository userExerciseRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service awsS3Service;

	public ExercisePopulationServiceImpl(UserExerciseRepository userExerciseRepository, MediaRepository mediaRepository,
			AwsS3Service awsS3Service) {
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
		if (exercises.isEmpty())
			return;

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

	private void fetchAndPopulateUserInteractionsAndCounts(List<ExerciseSummaryDto> exercises,
			List<Integer> exerciseIds) {
		int userId = AuthorizationUtil.getUserId();

		Map<Integer, SavesProjection> countsMap = userExerciseRepository
			.findCountsAndInteractionsByExerciseIds(userId, exerciseIds)
			.stream()
			.collect(Collectors.toMap(SavesProjection::getEntityId, projection -> projection));

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
		List<String> imageUrls = mediaRepository
			.findByParentIdAndParentType(exercise.getId(), MediaConnectedEntity.EXERCISE)
			.stream()
			.map(media -> awsS3Service.getImage(media.getImageName()))
			.toList();
		exercise.setImageUrls(imageUrls);
	}

}
