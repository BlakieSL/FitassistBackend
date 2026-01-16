package com.fitassist.backend.service.declaration.exercise;

import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;

import java.util.List;

public interface ExercisePopulationService {

	void populate(ExerciseResponseDto exercise);

	void populate(List<ExerciseSummaryDto> exercises);

}
