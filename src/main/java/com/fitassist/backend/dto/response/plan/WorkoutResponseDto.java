package com.fitassist.backend.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutResponseDto implements Serializable {

	private Integer id;

	private String name;

	private Short duration;

	private Short orderIndex;

	private Byte restDaysAfter;

	private Integer weekIndex;

	private Integer dayOfWeekIndex;

	private List<WorkoutSetResponseDto> workoutSets;

}
