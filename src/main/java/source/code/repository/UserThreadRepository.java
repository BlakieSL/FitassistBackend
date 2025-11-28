package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.thread.ForumThreadCountsProjection;
import source.code.model.user.UserThread;

import java.util.List;
import java.util.Optional;

public interface UserThreadRepository
        extends JpaRepository<UserThread, Integer>
{
    boolean existsByUserIdAndForumThreadId(int userId, int forumThreadId);
    Optional<UserThread> findByUserIdAndForumThreadId(int userId, int forumThreadId);

    @Query("""
           SELECT ut FROM UserThread ut
           JOIN FETCH ut.forumThread ft
           JOIN FETCH ft.user
           LEFT JOIN FETCH ft.userThreads
           LEFT JOIN FETCH ft.comments
           WHERE ut.user.id = :userId
           """)
    List<UserThread> findAllByUserId(@Param("userId") int userId);

    @Query("""
        SELECT
            ut.forumThread.id as threadId,
            COUNT(ut) as savesCount
        FROM UserThread ut
        WHERE ut.forumThread.id IN :threadIds
        GROUP BY ut.forumThread.id
    """)
    List<ForumThreadCountsProjection> findSavesCountsByThreadIds(@Param("threadIds") List<Integer> threadIds);

    @Query(value = """
        SELECT ut
        FROM UserThread ut
        JOIN FETCH ut.forumThread ft
        JOIN FETCH ft.user
        WHERE ut.user.id = :userId
    """)
    Page<UserThread> findByUserIdWithThread(@Param("userId") int userId, Pageable pageable);
}
