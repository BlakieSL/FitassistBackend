package source.code.dto.pojo.projection.recipe;

public interface RecipeCountsProjection {
    Integer getRecipeId();
    Long getLikesCount();
    Long getDislikesCount();
    Long getSavesCount();
}
