package source.code.dto.pojo.projection.comment;

public interface CommentCountsProjection {
    Integer getCommentId();
    Long getLikesCount();
    Long getDislikesCount();
    Long getIsLiked();
    Long getIsDisliked();

    default boolean isLiked() {
        return getIsLiked() != null && getIsLiked() == 1;
    }

    default boolean isDisliked() {
        return getIsDisliked() != null && getIsDisliked() == 1;
    }
}
