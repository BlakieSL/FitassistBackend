package com.fitassist.backend.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.dto.response.text.TextResponseDto;

import java.io.Serializable;
import java.util.List;

/**
 * fetched with db (findByIdWithDetails) -> mapper -> populated in createExercise and
 * getExercise
 *
 * <p>
 * Mapper sets: id, name, description, expertiseLevel, equipment, mechanicsType,
 * forceType, targetMuscles, instructions, tips Population sets: imageUrls, savesCount,
 * saved plans - set manually in getExercise via
 * planRepository.findByExerciseIdWithDetails -> planMapper -> planPopulationService
 *
 * <p>
 * saved - when user not authenticated (userId=-1), always false since query matches on
 * userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponseDto implements Serializable {

	private Integer id;

	private String name;

	private String description;

	private CategoryResponseDto expertiseLevel;

	private CategoryResponseDto equipment;

	private CategoryResponseDto mechanicsType;

	private CategoryResponseDto forceType;

	private List<TargetMuscleResponseDto> targetMuscles;

	private List<String> imageUrls;

	private List<PlanSummaryDto> plans;

	private List<TextResponseDto> instructions;

	private List<TextResponseDto> tips;

	private long savesCount;

	private boolean saved;

}
