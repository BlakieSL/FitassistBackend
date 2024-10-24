package source.code.service.declaration.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.response.Text.ExerciseInstructionResponseDto;
import source.code.dto.response.Text.ExerciseTipResponseDto;

import java.util.List;

public interface ExerciseInstructionsAndTipsService {
  void deleteInstruction(int instructionId);

  void deleteTip(int tipId);

  void updateInstruction(int instructionId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException;

  void updateTip(int tipId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException;

  List<ExerciseInstructionResponseDto> getInstructions(int exerciseId);

  List<ExerciseTipResponseDto> getTips(int exerciseId);
}
