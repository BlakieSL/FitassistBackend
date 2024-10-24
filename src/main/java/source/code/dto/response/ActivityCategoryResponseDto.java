package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCategoryResponseDto {
  private Integer id;
  private String name;
  private String iconUrl;
  private String gradient;

  public static ActivityCategoryResponseDto createWithIdName(
          int id, String name) {

    ActivityCategoryResponseDto activityCategoryResponseDto = new ActivityCategoryResponseDto();
    activityCategoryResponseDto.setId(id);
    activityCategoryResponseDto.setName(name);

    return activityCategoryResponseDto;
  }
}
