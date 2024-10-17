package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.ExerciseUpdateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.helper.enumerators.ExerciseField;

import java.util.List;

public interface ExerciseService {
  ExerciseResponseDto createExercise(ExerciseCreateDto dto);

  void updateExercise(int exerciseId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteExercise(int exerciseId);

  ExerciseResponseDto getExercise(int id);

  List<ExerciseResponseDto> getAllExercises();

  List<ExerciseResponseDto> searchExercises(SearchRequestDto dto);

  List<ExerciseResponseDto> getExercisesByCategory(int categoryId);

  List<ExerciseResponseDto> getExercisesByField(ExerciseField field, int value);
}

