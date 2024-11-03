package source.code.service.Declaration.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Exercise.ExerciseCreateDto;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Response.ExerciseResponseDto;
import source.code.model.Exercise.Exercise;

import java.util.List;

public interface ExerciseService {
  ExerciseResponseDto createExercise(ExerciseCreateDto dto);

  void updateExercise(int exerciseId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deleteExercise(int exerciseId);

  ExerciseResponseDto getExercise(int id);

  List<ExerciseResponseDto> getAllExercises();

  List<ExerciseResponseDto> getFilteredExercises(FilterDto filter);

  List<Exercise> getAllExerciseEntities();

  List<ExerciseResponseDto> getExercisesByCategory(int categoryId);
}

