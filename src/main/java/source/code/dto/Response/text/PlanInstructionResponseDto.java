package source.code.dto.Response.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanInstructionResponseDto implements BaseTextResponseDto {
    protected String title;
    private Integer id;
    private short number;
    private String text;
}
