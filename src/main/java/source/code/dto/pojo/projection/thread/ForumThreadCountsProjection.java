package source.code.dto.pojo.projection.thread;

public interface ForumThreadCountsProjection {
    Integer getThreadId();
    Long getIsSaved();
    Long getSavesCount();
    Long getCommentsCount();

    default boolean isSaved() {
        return getIsSaved() != null && getIsSaved() == 1;
    }

    default long savesCount() {
        return getSavesCount() != null ? getSavesCount() : 0L;
    }

    default long commentsCount() {
        return getCommentsCount() != null ? getCommentsCount() : 0L;
    }
}
