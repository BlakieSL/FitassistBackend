package source.code.service.declaration;

import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.helper.enumerators.ExerciseField;

import java.util.List;

public interface ExerciseService {
  ExerciseResponseDto createExercise(ExerciseCreateDto dto);

  ExerciseResponseDto getExercise(int id);

  List<ExerciseResponseDto> getAllExercises();

  List<ExerciseResponseDto> getExercisesByUserAndType(int userId, short type);

  List<ExerciseResponseDto> searchExercises(SearchRequestDto dto);

  List<ExerciseCategoryResponseDto> getAllCategories();

  List<ExerciseResponseDto> getExercisesByCategory(int categoryId);

  List<ExerciseResponseDto> getExercisesByField(ExerciseField field, int value);
}

