package source.code.service.declaration.exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.exercise.ExerciseCreateDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.model.exercise.Exercise;

import java.util.List;

public interface ExerciseService {
    ExerciseSummaryDto createExercise(ExerciseCreateDto dto);

    void updateExercise(int exerciseId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deleteExercise(int exerciseId);

    ExerciseResponseDto getExercise(int id);

    List<ExerciseSummaryDto> getAllExercises();

    List<ExerciseSummaryDto> getFilteredExercises(FilterDto filter);

    List<Exercise> getAllExerciseEntities();

    List<ExerciseSummaryDto> getExercisesByCategory(int categoryId);
}

