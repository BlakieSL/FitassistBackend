package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.ActivityCalculatedResponseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyActivitiesResponseDto {
    private int totalCaloriesBurned;
    List<ActivityCalculatedResponseDto> activities;
}
