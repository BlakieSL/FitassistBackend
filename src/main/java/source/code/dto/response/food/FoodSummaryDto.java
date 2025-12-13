package source.code.dto.response.food;

import lombok.*;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredFoods
 * fetched with db (findByRecipeId) -> mapper -> populated in RecipeFoodService.getFoodsByRecipe
 * fetched with db (findAllByUserIdWithMedia) -> mapper + set interactedWithAt -> populated in UserFoodService.getAllFromUser
 *
 * Mapper sets: id, name, calories, protein, fat, carbohydrates, category, imageName (from mediaList)
 * Population sets: firstImageUrl, savesCount, saved
 *
 * userFoodInteractionCreatedAt - only set in UserFoodService.getAllFromUser
 * saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class FoodSummaryDto implements BaseUserEntity, Serializable {
    private Integer id;
    @ToString.Include
    private String name;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrates;
    private CategoryResponseDto category;
    private String imageName;
    private String firstImageUrl;

    private LocalDateTime interactionCreatedAt;

    private long savesCount;

    private Boolean saved;
}