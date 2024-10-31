package source.code.dto.Request.Exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.Request.Text.ExerciseInstructionCreateDto;
import source.code.dto.Request.Text.ExerciseTipCreateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseCreateDto {
  private static final int MAX_NAME_LENGTH = 100;
  private static final int MAX_DESCRIPTION_LENGTH = 255;
  private static final int MAX_TEXT_LENGTH = 1000;

  @NotBlank
  @Size(max = MAX_NAME_LENGTH)
  private String name;

  @NotBlank
  @Size(max = MAX_DESCRIPTION_LENGTH)
  private String description;

  @NotBlank
  @Size(max = MAX_TEXT_LENGTH)
  private String text;

  @NotNull
  private int exerciseTypeId;

  @NotNull
  private int equipmentId;

  @NotNull
  private int expertiseLevelId;

  @NotNull
  private int mechanicsTypeId;

  @NotNull
  private int forceTypeId;

  private List<Integer> categoryIds;

  private List<ExerciseInstructionCreateDto> instructions;

  private List<ExerciseTipCreateDto> tips;

  public static ExerciseCreateDto createWithName(String name) {
    ExerciseCreateDto createDto = new ExerciseCreateDto();
    createDto.setName(name);
    return createDto;
  }
}
