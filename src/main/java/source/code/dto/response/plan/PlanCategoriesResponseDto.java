package source.code.dto.response.plan;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.Enum.model.PlanStructureType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCategoriesResponseDto implements Serializable {

	private List<PlanStructureType> structureTypes;

	private List<CategoryResponseDto> categories;

	private List<CategoryResponseDto> equipments;

}
