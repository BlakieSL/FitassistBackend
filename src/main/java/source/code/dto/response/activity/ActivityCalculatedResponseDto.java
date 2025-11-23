package source.code.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCalculatedResponseDto implements Serializable {
    private Integer id;
    private String name;
    private BigDecimal met;
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
