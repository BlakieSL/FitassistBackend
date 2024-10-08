package source.code.service.declaration;

import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;

import java.util.List;

public interface ExerciseService {
    ExerciseResponseDto createExercise(ExerciseCreateDto dto);
    ExerciseResponseDto getExercise(int id);
    List<ExerciseResponseDto> getAllExercises();
    List<ExerciseResponseDto> getExercisesByUser(int userId);
    List<ExerciseResponseDto> searchExercises(SearchRequestDto dto);
    List<ExerciseCategoryResponseDto> getAllCategories();
    List<ExerciseResponseDto> getExercisesByCategory(int categoryId);
    List<ExerciseResponseDto> getExercisesByExpertiseLevel(int expertiseLevelId);
    List<ExerciseResponseDto> getExercisesByForceType(int forceTypeId);
    List<ExerciseResponseDto> getExercisesByMechanicsType(int mechanicsTypeId);
    List<ExerciseResponseDto> getExercisesByEquipment(int exerciseEquipmentId);
    List<ExerciseResponseDto> getExercisesByType(int exerciseTypeId);
    List<ExerciseInstructionResponseDto> getExerciseInstructions(int exerciseId);
    List<ExerciseTipResponseDto> getExerciseTips(int exerciseId);
}

