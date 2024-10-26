package source.code.dto.Response.Text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeInstructionResponseDto implements BaseTextResponseDto {
  private Integer id;
  private short number;
  private String title;
  private String text;

  public static RecipeInstructionResponseDto createWithId(int id) {
    RecipeInstructionResponseDto responseDto = new RecipeInstructionResponseDto();
    responseDto.setId(id);
    return  responseDto;
  }
}
