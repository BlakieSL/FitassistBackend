package source.code.dto.response.daily;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyActivitiesResponseDto implements Serializable {

	List<ActivityCalculatedResponseDto> activities;

	private BigDecimal totalCaloriesBurned;

}
