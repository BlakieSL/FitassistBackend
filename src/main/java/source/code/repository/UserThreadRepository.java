package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.thread.ForumThreadCountsProjection;
import source.code.dto.pojo.projection.thread.ForumThreadUserInteractionProjection;
import source.code.model.user.UserThread;

import java.util.List;
import java.util.Optional;

public interface UserThreadRepository extends JpaRepository<UserThread, Integer> {
    boolean existsByUserIdAndForumThreadId(int userId, int forumThreadId);

    Optional<UserThread> findByUserIdAndForumThreadId(int userId, int forumThreadId);

    @Query(value = """
        SELECT ut
        FROM UserThread ut
        JOIN FETCH ut.forumThread ft
        JOIN FETCH ft.user
        WHERE ut.user.id = :userId
    """)
    Page<UserThread> findByUserIdWithThread(@Param("userId") int userId, Pageable pageable);

    @Query("""
        SELECT
            ut.forumThread.id as threadId,
            COUNT(ut) as savesCount
        FROM UserThread ut
        WHERE ut.forumThread.id IN :threadIds
        GROUP BY ut.forumThread.id
    """)
    List<ForumThreadCountsProjection> findSavesCountsByThreadIds(@Param("threadIds") List<Integer> threadIds);

    @Query("""
        SELECT
            MAX(CASE WHEN ut.user.id = :userId THEN 1 ELSE 0 END) as isSaved,
            COUNT(ut) as savesCount,
            (SELECT COUNT(c) FROM Comment c WHERE c.thread.id = :threadId) as commentsCount
        FROM UserThread ut
        WHERE ut.forumThread.id = :threadId
    """)
    ForumThreadUserInteractionProjection findUserInteractionAndCounts(@Param("userId") int userId,
                                                                      @Param("threadId") int threadId);
}