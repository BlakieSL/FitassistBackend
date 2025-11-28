package source.code.dto.pojo.projection.comment;

public interface CommentCountsProjection {
    Integer getCommentId();
    Long getLikesCount();
    Long getDislikesCount();
}
