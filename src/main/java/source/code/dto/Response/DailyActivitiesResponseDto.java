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
}
