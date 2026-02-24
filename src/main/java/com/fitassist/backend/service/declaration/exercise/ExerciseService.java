package com.fitassist.backend.service.declaration.exercise;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.exercise.ExerciseCategoriesResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.model.exercise.Exercise;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExerciseService {

	ExerciseResponseDto createExercise(ExerciseCreateDto dto);

	void updateExercise(int exerciseId, JsonMergePatch patch) throws JacksonException;

	void deleteExercise(int exerciseId);

	ExerciseResponseDto getExercise(int id);

	Page<ExerciseSummaryDto> getFilteredExercises(FilterDto filter, Pageable pageable);

	List<Exercise> getAllExerciseEntities();

	ExerciseCategoriesResponseDto getAllExerciseCategories();

}
