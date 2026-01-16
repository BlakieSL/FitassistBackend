package com.fitassist.backend.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCategoriesResponseDto implements Serializable {

	private List<CategoryResponseDto> equipments;

	private List<CategoryResponseDto> expertiseLevels;

	private List<CategoryResponseDto> forceTypes;

	private List<CategoryResponseDto> mechanicsTypes;

	private List<CategoryResponseDto> targetMuscles;

}
