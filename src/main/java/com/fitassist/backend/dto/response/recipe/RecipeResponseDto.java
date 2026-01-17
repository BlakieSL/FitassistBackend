package com.fitassist.backend.dto.response.recipe;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.pojo.RecipeFoodDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.text.TextResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * fetched with db (findByIdWithDetails) -> mapper -> populated in createRecipe and
 * getRecipe
 *
 * <p>
 * Mapper sets: id, name, description, isPublic, createdAt, minutesToPrepare, views,
 * author (id, username), categories, instructions, foods, totalCalories Population sets:
 * imageUrls, author.imageName/imageUrl, likesCount, dislikesCount, savesCount, liked,
 * disliked, saved
 *
 * <p>
 * liked/disliked/saved - when user not authenticated (userId=-1), always false since
 * query matches on userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto implements Serializable {

	private Integer id;

	private String name;

	private String description;

	private Boolean isPublic;

	private LocalDateTime createdAt;

	private short minutesToPrepare;

	private long views;

	private AuthorDto author;

	private long likesCount;

	private long dislikesCount;

	private long savesCount;

	private boolean liked;

	private boolean disliked;

	private boolean saved;

	private BigDecimal totalCalories;

	private List<RecipeFoodDto> foods;

	private List<TextResponseDto> instructions;

	private List<CategoryResponseDto> categories;

	private List<String> imageUrls;

}
