package source.code.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.CategoryDto;
import source.code.helper.BaseUserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSummaryDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private String description;
    private String imageName;
    private String firstImageUrl;
    private CategoryDto expertiseLevel;
    private CategoryDto equipment;
    private CategoryDto mechanicsType;
    private CategoryDto forceType;
    private LocalDateTime userExerciseInteractionCreatedAt;
}