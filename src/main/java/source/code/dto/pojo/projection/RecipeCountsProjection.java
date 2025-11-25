package source.code.dto.pojo.projection;

public interface RecipeCountsProjection {
    Integer getRecipeId();
    Long getLikesCount();
    Long getDislikesCount();
    Long getSavesCount();
}
