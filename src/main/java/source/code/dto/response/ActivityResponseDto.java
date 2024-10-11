package source.code.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityResponseDto {
  private Integer id;
  private String name;
  private double met;
  private String categoryName;
  private int categoryId;

  public static ActivityResponseDto createWithIdCategoryNameCategoryId(
          int id, String categoryName, int categoryId) {

    ActivityResponseDto activityResponseDto = new ActivityResponseDto();
    activityResponseDto.setId(id);
    activityResponseDto.setCategoryName(categoryName);
    activityResponseDto.setCategoryId(categoryId);

    return activityResponseDto;
  }
}
