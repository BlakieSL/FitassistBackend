package source.code.dto.request.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeInstructionUpdateDto {
    private short orderIndex;
    private short title;
    private String text;
}
