package source.code.service.declaration.Exercise;

import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;

import java.util.List;

public interface ExerciseInstructionsAndTipsService {
  List<ExerciseInstructionResponseDto> getExerciseInstructions(int exerciseId);

  List<ExerciseTipResponseDto> getExerciseTips(int exerciseId);
}
