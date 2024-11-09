package source.code.dto.response.activity;

import lombok.*;
import source.code.model.user.BaseUserEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityResponseDto implements BaseUserEntity {
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
