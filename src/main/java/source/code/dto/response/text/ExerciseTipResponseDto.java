package source.code.dto.response.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTipResponseDto implements BaseTextResponseDto {
    private Integer id;
    private short number;
    private String text;

    public static ExerciseTipResponseDto createWithId(int id) {
        ExerciseTipResponseDto responseDto = new ExerciseTipResponseDto();
        responseDto.setId(id);
        return responseDto;
    }
}
