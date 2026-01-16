package com.fitassist.backend.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.plan.PlanStructureType;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCategoriesResponseDto implements Serializable {

	private List<PlanStructureType> structureTypes;

	private List<CategoryResponseDto> categories;

	private List<CategoryResponseDto> equipments;

}
