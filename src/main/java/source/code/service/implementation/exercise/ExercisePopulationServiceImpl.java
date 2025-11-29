package source.code.service.implementation.exercise;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserExerciseRepository;
import source.code.service.declaration.exercise.ExercisePopulationService;

@Service
public class ExercisePopulationServiceImpl implements ExercisePopulationService {
    private final UserExerciseRepository userExerciseRepository;

    public ExercisePopulationServiceImpl(UserExerciseRepository userExerciseRepository) {
        this.userExerciseRepository = userExerciseRepository;
    }

    @Override
    public void populate(ExerciseResponseDto exercise) {
        int userId = AuthorizationUtil.getUserId();
        SavesProjection savesData = userExerciseRepository.findSavesCountAndUserSaved(exercise.getId(), userId);
        exercise.setSavesCount(savesData.savesCount());
        exercise.setSaved(savesData.isSaved());
    }
}
