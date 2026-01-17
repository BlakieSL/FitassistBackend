package com.fitassist.backend.dto.pojo;

import com.fitassist.backend.dto.response.food.IngredientResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFoodDto {

	private int id;

	private BigDecimal quantity;

	private IngredientResponseDto ingredient;

}
