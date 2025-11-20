package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @EntityGraph(value = "Comment.withoutAssociations")
    List<Comment> findAllByThreadIdAndParentCommentNull(int threadId);

    List<Comment> findAllByParentCommentId(int commentId);

    long countAllByThreadId(int threadId);

    List<Comment> findAllByUser_Id(Integer userId);

    @EntityGraph(value = "Comment.withoutAssociations")
    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findByIdWithoutAssociations(Integer id);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM comment WHERE id = :id")
    void deleteCommentDirectly(int id);

    @Query(value = """
    WITH RECURSIVE comment_tree AS (
        SELECT
            c.id,
            c.text,
            c.thread_id,
            c.user_id,
            c.parent_comment_id
        FROM comment c
        WHERE c.parent_comment_id = :commentId

        UNION ALL

        SELECT
            c.id,
            c.text,
            c.thread_id,
            c.user_id,
            c.parent_comment_id
        FROM comment c
        INNER JOIN comment_tree ct ON c.parent_comment_id = ct.id
    )
    SELECT *
    FROM comment_tree
    ORDER BY id
    """, nativeQuery = true)
    List<Object[]> findCommentHierarchy(Integer commentId);

    @Query("""
      SELECT new source.code.dto.response.comment.CommentSummaryDto(
             c.id,
             c.text,
             c.dateCreated,
             c.user.username,
             c.user.id,
             (SELECT m.imageName FROM Media m
              WHERE m.parentId = c.user.id
              AND m.parentType = 'USER'
              ORDER BY m.id ASC
              LIMIT 1),
             null,
             SIZE(c.userCommentLikes) ,
             SIZE(c.replies),
             null)
      FROM Comment c
      WHERE c.user.id = :userId
    """)
    List<CommentSummaryDto> findSummaryByUserId(Integer userId);

    @Query("""
      SELECT new source.code.dto.response.comment.CommentSummaryDto(
             c.id,
             c.text,
             c.dateCreated,
             u.username,
             u.id,
             (SELECT m.imageName FROM Media m
              WHERE m.parentId = u.id
              AND m.parentType = 'USER'
              ORDER BY m.id ASC
              LIMIT 1),
             null,
             CAST((SELECT COUNT(uc2) FROM UserComment uc2 WHERE uc2.comment.id = c.id AND uc2.type = 'LIKE') -
                  (SELECT COUNT(uc3) FROM UserComment uc3 WHERE uc3.comment.id = c.id AND uc3.type = 'DISLIKE') AS int),
             CAST((SELECT COUNT(cr) FROM Comment cr WHERE cr.parentComment.id = c.id) AS int),
             CASE WHEN :fetchByInteraction = true THEN uc.createdAt ELSE null END)
      FROM Comment c
      JOIN c.user u
      LEFT JOIN UserComment uc ON uc.comment.id = c.id AND uc.user.id = :userId AND (:type IS NULL OR uc.type = :type)
      WHERE (:fetchByInteraction = false AND c.user.id = :userId) OR
            (:fetchByInteraction = true AND uc.id IS NOT NULL)
      ORDER BY CASE WHEN :fetchByInteraction = true THEN uc.createdAt ELSE c.dateCreated END DESC
    """)
    List<CommentSummaryDto> findCommentSummaryUnified(@Param("userId") int userId,
                                                       @Param("type") TypeOfInteraction type,
                                                       @Param("fetchByInteraction") boolean fetchByInteraction);

}
