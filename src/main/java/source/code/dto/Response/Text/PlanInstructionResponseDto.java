package source.code.dto.Response.Text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanInstructionResponseDto implements BaseTextResponseDto {
  private Integer id;
  private short number;
  protected String title;
  private String text;
}
