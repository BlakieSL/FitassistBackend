package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.model.thread.Comment;


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
             null,
             SIZE(c.userCommentLikes) ,
             SIZE(c.replies))
      FROM Comment c
      WHERE c.user.id = :userId
    """)
    List<CommentSummaryDto> findSummaryByUserId(Integer userId);

}
