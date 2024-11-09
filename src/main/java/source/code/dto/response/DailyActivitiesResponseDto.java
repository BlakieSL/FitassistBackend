package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyActivitiesResponseDto {
    List<ActivityCalculatedResponseDto> activities;
    private int totalCaloriesBurned;
}
