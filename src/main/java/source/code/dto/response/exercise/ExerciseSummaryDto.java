package source.code.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseSummaryDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private String imageName;
    private String firstImageUrl;
}