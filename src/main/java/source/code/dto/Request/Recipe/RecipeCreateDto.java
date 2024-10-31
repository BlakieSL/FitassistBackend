package source.code.dto.Request.Recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.Request.Text.RecipeInstructionCreateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCreateDto {
  private static final int NAME_MAX_LENGTH = 100;
  private static final int DESCRIPTION_MAX_LENGTH = 255;
  private static final int TEXT_MAX_LENGTH = 2000;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @NotBlank
  @Size(max = DESCRIPTION_MAX_LENGTH)
  private String description;

  @NotBlank
  @Size(max = TEXT_MAX_LENGTH)
  private String text;

  private List<Integer> categoryIds;

  private List<RecipeInstructionCreateDto> instructions;
}
