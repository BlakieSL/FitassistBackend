package source.code.dto.Request.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanInstructionUpdateDto {
    private short number;
    private String title;
    private String text;
}
