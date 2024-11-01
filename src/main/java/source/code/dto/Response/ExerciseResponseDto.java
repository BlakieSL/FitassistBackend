package source.code.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.Other.ExerciseCategoryShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponseDto {
  private Integer id;
  private String name;
  private String description;
  private String text;
  private ExerciseCategoryShortDto expertiseLevel;
  private ExerciseCategoryShortDto equipment;
  private ExerciseCategoryShortDto mechanicsType;
  private ExerciseCategoryShortDto forceType;
  private List<ExerciseCategoryShortDto> targetMuscles;

  public static ExerciseResponseDto createWithIdAndName(int id, String name) {
    ExerciseResponseDto responseDto = new ExerciseResponseDto();
    responseDto.setId(id);
    responseDto.setName(name);
    return responseDto;
  }
}
