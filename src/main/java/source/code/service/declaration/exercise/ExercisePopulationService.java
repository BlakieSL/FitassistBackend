package source.code.service.declaration.exercise;

import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;

import java.util.List;

public interface ExercisePopulationService {
    void populate(ExerciseResponseDto exercise);
    void populate(List<ExerciseSummaryDto> exercises);
}
