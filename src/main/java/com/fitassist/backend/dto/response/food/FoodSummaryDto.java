package com.fitassist.backend.dto.response.food;

import lombok.*;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredFoods fetched with db
 * (findByRecipeId) -> mapper -> populated in RecipeFoodService.getFoodsByRecipe fetched
 * with db (findAllByUserIdWithMedia) -> mapper + set interactedWithAt -> populated in
 * UserFoodService.getAllFromUser
 *
 * <p>
 * Mapper sets: id, name, foodMacros, category, imageName (from mediaList) Population
 * sets: firstImageUrl, savesCount, saved
 *
 * <p>
 * userFoodInteractionCreatedAt - only set in UserFoodService.getAllFromUser saved - when
 * user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class FoodSummaryDto implements UserEntitySummaryResponseDto, Serializable {

	private Integer id;

	@ToString.Include
	private String name;

	private FoodMacros foodMacros;

	private CategoryResponseDto category;

	private String imageName;

	private String firstImageUrl;

	private LocalDateTime interactionCreatedAt;

	private long savesCount;

	private Boolean saved;

}
