package source.code.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCategoryResponseDto {
  private Integer id;
  private String name;
  private String iconUrl;
  private String gradient;

  public static ExerciseCategoryResponseDto createWithId(int id) {
    ExerciseCategoryResponseDto responseDto = new ExerciseCategoryResponseDto();
    responseDto.setId(id);
    return responseDto;
  }
}
