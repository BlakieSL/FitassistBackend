package source.code.dto.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCategoryShortDto {
  private Integer id;
  private String name;
  private int priority;

  public ExerciseCategoryShortDto(Integer id, String name) {
    this.id = id;
    this.name = name;
  }
}
