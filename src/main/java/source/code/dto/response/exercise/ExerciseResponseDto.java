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
    private long savesCount;
    private boolean saved;
    private List<ExerciseInstructionResponseDto> instructions;
    private List<ExerciseTipResponseDto> tips;
}
