package source.code.dto.Request.Text;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanInstructionCreateDto {
    @NotNull
    private short number;
    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
