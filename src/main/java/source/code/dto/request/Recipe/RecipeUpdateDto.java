package source.code.dto.request.Recipe;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeUpdateDto {
  private static final int NAME_MAX_LENGTH = 100;
  private static final int DESCRIPTION_MAX_LENGTH = 255;
  private static final int TEXT_MAX_LENGTH = 2000;

  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @Size(max = DESCRIPTION_MAX_LENGTH)
  private String description;

  @Size(max = TEXT_MAX_LENGTH)
  private String text;

  private Double score;

  private List<Integer> categoryIds;
}
