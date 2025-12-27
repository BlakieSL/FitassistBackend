package source.code.dto.response.exercise;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import source.code.dto.response.category.CategoryResponseDto;

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
