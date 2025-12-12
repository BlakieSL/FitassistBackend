package source.code.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSummaryDto implements BaseUserEntity, Serializable {
    private Integer id;
    private String name;
    private String description;
    private String imageName;
    private String firstImageUrl;
    private CategoryResponseDto expertiseLevel;
    private CategoryResponseDto equipment;
    private CategoryResponseDto mechanicsType;
    private CategoryResponseDto forceType;

    private LocalDateTime userExerciseInteractionCreatedAt;
}