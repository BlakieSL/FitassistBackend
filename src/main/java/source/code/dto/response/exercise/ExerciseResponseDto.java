package source.code.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.text.ExerciseInstructionResponseDto;
import source.code.dto.response.text.ExerciseTipResponseDto;

import java.io.Serializable;
import java.util.List;

/**
 * fetched with db (findByIdWithDetails) -> mapper -> populated in createExercise and getExercise
 *
 * Mapper sets: id, name, description, expertiseLevel, equipment, mechanicsType, forceType, targetMuscles, instructions, tips
 * Population sets: imageUrls, savesCount, saved
 * plans - set manually in getExercise via planRepository.findByExerciseIdWithDetails -> planMapper -> planPopulationService
 *
 * saved - when user not authenticated (userId=-1), always false since query matches on userId
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
    private List<ExerciseInstructionResponseDto> instructions;
    private List<ExerciseTipResponseDto> tips;
    private long savesCount;
    private boolean saved;
}
