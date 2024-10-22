package source.code.service.declaration.Exercise;

import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;

import java.util.List;

public interface ExerciseInstructionsAndTipsService {
  List<ExerciseInstructionResponseDto> getInstructions(int exerciseId);

  List<ExerciseTipResponseDto> getTips(int exerciseId);
}
