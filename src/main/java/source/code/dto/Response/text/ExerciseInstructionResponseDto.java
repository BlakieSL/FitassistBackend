package source.code.dto.Response.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseInstructionResponseDto implements BaseTextResponseDto {
    private Integer id;
    private short number;
    private String text;

    public static ExerciseInstructionResponseDto createWithId(int id) {
        ExerciseInstructionResponseDto responseDto = new ExerciseInstructionResponseDto();
        responseDto.setId(id);
        return responseDto;
    }
}
