package source.code.service.declaration;

import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;

import java.util.List;

public interface UserExerciseService {
  void saveExerciseToUser(int exerciseId, int userId, short type);

  void deleteSavedExerciseFromUser(int exerciseId, int userId, short type);

  List<ExerciseResponseDto> getExercisesByUserAndType(int userId, short type);

  LikesAndSavesResponseDto calculateExerciseLikesAndSaves(int exerciseId);
}
