package source.code.dto.Request.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeInstructionUpdateDto {
    private short number;
    private short title;
    private String text;
}
