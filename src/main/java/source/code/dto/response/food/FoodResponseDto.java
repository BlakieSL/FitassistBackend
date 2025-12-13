package source.code.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * fetched with db (findByIdWithMedia) -> mapper -> populated in createFood and getFood
 *
 * Mapper sets: id, name, calories, protein, fat, carbohydrates, category, imageUrls (via @AfterMapping)
 * Population sets: savesCount, saved
 * recipes - set manually only in getFood via recipeRepository.findAllWithDetailsByFoodId -> recipeMapper -> recipePopulationService
 *
 * saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodResponseDto implements Serializable {
    private Integer id;
    private String name;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrates;
    private CategoryResponseDto category;
    private List<String> imageUrls;
    private List<RecipeSummaryDto> recipes;

    private long savesCount;
    private boolean saved;
}
