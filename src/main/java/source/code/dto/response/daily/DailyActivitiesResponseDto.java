package source.code.dto.response.daily;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyActivitiesResponseDto implements Serializable {

	List<ActivityCalculatedResponseDto> activities;

	private int totalCaloriesBurned;

}
