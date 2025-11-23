package source.code.dto.response.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseInstructionResponseDto implements BaseTextResponseDto, Serializable {
    private Integer id;
    private short orderIndex;
    private String text;
}
