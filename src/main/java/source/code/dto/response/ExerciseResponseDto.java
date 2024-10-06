package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.other.ExerciseCategoryShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponseDto {
    private Integer id;
    private String name;
    private String description;
    private String text;
    private Double score;
    private ExerciseCategoryShortDto expertiseLevel;
    private ExerciseCategoryShortDto mechanicsType;
    private ExerciseCategoryShortDto forceType;
    private ExerciseCategoryShortDto exerciseEquipment;
    private ExerciseCategoryShortDto exerciseType;
    private List<ExerciseCategoryShortDto> categories;
}
