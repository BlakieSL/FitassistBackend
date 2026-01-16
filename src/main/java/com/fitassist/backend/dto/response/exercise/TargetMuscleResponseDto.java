package com.fitassist.backend.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetMuscleResponseDto {

	private Integer id;

	private String name;

	private BigDecimal priority;

	public static TargetMuscleResponseDto create(Integer id, String name, BigDecimal priority) {
		return new TargetMuscleResponseDto(id, name, priority);
	}

}
