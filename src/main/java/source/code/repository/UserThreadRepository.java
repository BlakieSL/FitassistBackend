package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
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
           SELECT new source.code.dto.response.forumThread.ForumThreadSummaryDto(
               ft.id,
               ft.title,
               ft.dateCreated,
               ft.text,
               ft.views,
               CAST((SELECT COUNT(ut2) FROM UserThread ut2 WHERE ut2.forumThread.id = ft.id) AS int),
               CAST((SELECT COUNT(c) FROM Comment c WHERE c.thread.id = ft.id) AS int),
               u.username,
               u.id,
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = u.id
                AND m.parentType = 'USER'
                ORDER BY m.id ASC
                LIMIT 1),
               null)
           FROM UserThread ut
           JOIN ut.forumThread ft
           JOIN ft.user u
           WHERE ut.user.id = :userId
           """)
    List<ForumThreadSummaryDto> findThreadSummaryByUserId(@Param("userId") int userId);
}
