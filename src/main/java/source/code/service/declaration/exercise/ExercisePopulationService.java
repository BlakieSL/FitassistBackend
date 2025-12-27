package source.code.service.declaration.exercise;

import java.util.List;

import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;

public interface ExercisePopulationService {

	void populate(ExerciseResponseDto exercise);

	void populate(List<ExerciseSummaryDto> exercises);

}
