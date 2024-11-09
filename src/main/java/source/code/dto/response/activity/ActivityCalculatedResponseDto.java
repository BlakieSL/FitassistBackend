package source.code.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCalculatedResponseDto {
    private Integer id;
    private String name;
    private double met;
    private String categoryName;
    private int categoryId;
    private int caloriesBurned;
    private int time;

    public static ActivityCalculatedResponseDto createWithId(int id, int time) {
        ActivityCalculatedResponseDto activityCalculatedResponseDto = new ActivityCalculatedResponseDto();
        activityCalculatedResponseDto.setId(id);
        activityCalculatedResponseDto.setTime(time);

        return activityCalculatedResponseDto;
    }
}
