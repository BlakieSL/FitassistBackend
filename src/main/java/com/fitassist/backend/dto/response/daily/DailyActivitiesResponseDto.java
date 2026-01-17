package com.fitassist.backend.dto.response.daily;

import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
