package source.code.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.TargetMuscleShortDto;
import source.code.helper.BaseUserEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponseDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private String description;
    private TargetMuscleShortDto expertiseLevel;
    private TargetMuscleShortDto equipment;
    private TargetMuscleShortDto mechanicsType;
    private TargetMuscleShortDto forceType;
    private List<TargetMuscleShortDto> targetMuscles;
    private String firstImageUrl;
}
