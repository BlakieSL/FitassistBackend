package com.fitassist.backend.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.pojo.MediaImagesDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;

import java.io.Serializable;
import java.util.List;

/**
 * fetched with db (findByIdWithMedia) -> mapper -> populated in createFood and getFood
 *
 * <p>
 * Mapper sets: id, name, foodMacros, category, images.imageNames Population sets:
 * images.imageUrls, savesCount, saved recipes - set manually only in getFood via
 * recipeRepository.findAllWithDetailsByFoodId -> recipeMapper -> recipePopulationService
 *
 * <p>
 * saved - when user not authenticated (userId=-1), always false since query matches on
 * userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodResponseDto implements Serializable {

	private Integer id;

	private String name;

	private FoodMacros foodMacros;

	private CategoryResponseDto category;

	private MediaImagesDto images;

	private List<RecipeSummaryDto> recipes;

	private long savesCount;

	private boolean saved;

}
