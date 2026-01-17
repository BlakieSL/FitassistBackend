package com.fitassist.backend.dto.response.plan;

import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.model.plan.PlanStructureType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
