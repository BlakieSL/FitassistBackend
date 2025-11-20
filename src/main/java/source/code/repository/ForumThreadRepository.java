package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.model.thread.ForumThread;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer> {
    List<ForumThread> findAllByThreadCategoryId(int categoryId);

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
             null,
             CASE WHEN :fetchByInteraction = true THEN ut.createdAt ELSE null END)
      FROM ForumThread ft
      JOIN ft.user u
      LEFT JOIN UserThread ut ON ut.forumThread.id = ft.id AND ut.user.id = :userId
      WHERE (:fetchByInteraction = false AND ft.user.id = :userId) OR
            (:fetchByInteraction = true AND ut.id IS NOT NULL)
      ORDER BY CASE WHEN :fetchByInteraction = true THEN ut.createdAt ELSE ft.dateCreated END DESC
    """)
    List<ForumThreadSummaryDto> findThreadSummaryUnified(@Param("userId") int userId,
                                                         @Param("fetchByInteraction") boolean fetchByInteraction);
}