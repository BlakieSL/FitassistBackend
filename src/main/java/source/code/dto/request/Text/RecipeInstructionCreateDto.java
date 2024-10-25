package source.code.dto.request.Text;

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
public class RecipeInstructionCreateDto {
  @NotNull
  private short number;
  @NotBlank
  private short title;
  @NotBlank
  private String text;
}
