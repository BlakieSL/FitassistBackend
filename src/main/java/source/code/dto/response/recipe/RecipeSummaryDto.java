package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredRecipes
 * fetched with db (findByIdsWithDetails) -> mapper -> populated in UserRecipeService.getAllFromUser
 * <p>
 * Mapper sets: id, name, description, isPublic, createdAt, minutesToPrepare, views, categories, author (id, username), firstImageName (from mediaList)
 * Population sets: firstImageUrl, author.imageName/imageUrl, likesCount, dislikesCount, savesCount, liked, disliked, saved, ingredientsCount
 * <p>
 * interactedWithAt - only set in UserRecipeService.getAllFromUser
 * liked/disliked/saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private short minutesToPrepare;
    private List<CategoryResponseDto> categories = new ArrayList<>();

    private String firstImageName;
    private String firstImageUrl;

    private AuthorDto author;

    private LocalDateTime interactionCreatedAt;

    private long views;
    private long likesCount;
    private long dislikesCount;
    private long savesCount;
    private long ingredientsCount;

    private Boolean liked;
    private Boolean disliked;
    private Boolean saved;
}