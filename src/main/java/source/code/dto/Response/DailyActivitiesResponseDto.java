package source.code.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyActivitiesResponseDto {
    List<ActivityCalculatedResponseDto> activities;
    private int totalCaloriesBurned;

    public static DailyActivitiesResponseDto of(
            List<ActivityCalculatedResponseDto> activities, int totalCaloriesBurned
    ) {
        return new DailyActivitiesResponseDto(activities, totalCaloriesBurned);
    }
}
