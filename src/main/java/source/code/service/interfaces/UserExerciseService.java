package source.code.service.interfaces;

import source.code.dto.response.LikesAndSavesResponseDto;

public interface UserExerciseService {
    void saveExerciseToUser(int exerciseId, int userId, short type);
    void deleteSavedExerciseFromUser(int exerciseId, int userId, short type);
    LikesAndSavesResponseDto calculateExerciseLikesAndSaves(int exerciseId);
}
