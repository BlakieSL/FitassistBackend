package source.code.dto.pojo.projection.comment;

public interface CommentCountsProjection {

	Integer getCommentId();

	Long getLikesCount();

	Long getDislikesCount();

	Long getIsLiked();

	Long getIsDisliked();

	Long getRepliesCount();

	default boolean isLiked() {
		return getIsLiked() != null && getIsLiked() == 1;
	}

	default boolean isDisliked() {
		return getIsDisliked() != null && getIsDisliked() == 1;
	}

	default long likesCount() {
		return getLikesCount() != null ? getLikesCount() : 0L;
	}

	default long dislikesCount() {
		return getDislikesCount() != null ? getDislikesCount() : 0L;
	}

	default long repliesCount() {
		return getRepliesCount() != null ? getRepliesCount() : 0L;
	}

}
