package source.code.dto.pojo.projection.recipe;

public interface RecipeAndPlanUserInteractionProjection {
    Long getIsLiked();
    Long getIsDisliked();
    Long getIsSaved();
    Long getLikesCount();
    Long getDislikesCount();
    Long getSavesCount();

    default boolean isLiked() {
        return getIsLiked() != null && getIsLiked() == 1;
    }

    default boolean isDisliked() {
        return getIsDisliked() != null && getIsDisliked() == 1;
    }

    default boolean isSaved() {
        return getIsSaved() != null && getIsSaved() == 1;
    }

    default long likesCount() {
        return getLikesCount() != null ? getLikesCount() : 0L;
    }

    default long dislikesCount() {
        return getDislikesCount() != null ? getDislikesCount() : 0L;
    }

    default long savesCount() {
        return getSavesCount() != null ? getSavesCount() : 0L;
    }
}
