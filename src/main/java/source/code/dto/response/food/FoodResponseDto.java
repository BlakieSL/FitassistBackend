package source.code.dto.response.food;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.pojo.MediaImagesDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

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
