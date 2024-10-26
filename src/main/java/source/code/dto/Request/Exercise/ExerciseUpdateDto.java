package source.code.dto.Request.Exercise;

import jakarta.validation.constraints.Positive;
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
public class ExerciseUpdateDto {
  private static final int MAX_NAME_LENGTH = 100;
  private static final int MAX_DESCRIPTION_LENGTH = 255;
  private static final int MAX_TEXT_LENGTH = 1000;

  @Size(max = MAX_NAME_LENGTH)
  private String name;

  @Size(max = MAX_DESCRIPTION_LENGTH)
  private String description;

  @Size(max = MAX_TEXT_LENGTH)
  private String text;

  @Positive
  private Double score;

  private Integer expertiseLevelId;
  private Integer mechanicsTypeId;
  private Integer forceTypeId;
  private Integer exerciseEquipmentId;
  private Integer exerciseTypeId;
  private List<Integer> categoryIds;
}
