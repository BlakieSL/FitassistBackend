package source.code.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.POJO.RecipeCategoryShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto {
  List<RecipeCategoryShortDto> categories;
  private Integer id;
  private String name;
  private String description;
  private String text;
}
