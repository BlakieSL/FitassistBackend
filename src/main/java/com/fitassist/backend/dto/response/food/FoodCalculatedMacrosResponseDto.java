package com.fitassist.backend.dto.response.food;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCalculatedMacrosResponseDto implements Serializable {

	private Integer dailyItemId;

	private Integer id;

	private String name;

	private FoodMacros foodMacros;

	private CategoryResponseDto category;

	private BigDecimal quantity;

}
