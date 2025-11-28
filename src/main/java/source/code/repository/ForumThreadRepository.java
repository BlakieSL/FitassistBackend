package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.thread.ForumThreadCommentsCountProjection;
import source.code.model.thread.ForumThread;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer> {
    List<ForumThread> findAllByThreadCategoryId(int categoryId);

    @Query(value = """
      SELECT ft
      FROM ForumThread ft
      JOIN FETCH ft.user u
      WHERE ft.user.id = :userId
    """)
    Page<ForumThread> findCreatedByUserWithDetails(@Param("userId") int userId, Pageable pageable);

    @Query("""
      SELECT
           c.thread.id as threadId,
           COUNT(c) as commentsCount
      FROM Comment c
      WHERE c.thread.id IN :threadIds
      GROUP BY c.thread.id
    """)
    List<ForumThreadCommentsCountProjection> findCommentsCountsByThreadIds(@Param("threadIds") List<Integer> threadIds);
}