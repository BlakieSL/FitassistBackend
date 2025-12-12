package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import source.code.model.thread.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    @EntityGraph(value = "Comment.summary")
    @NotNull
    Page<Comment> findAll(Specification<Comment> spec, @NotNull Pageable pageable);

    @EntityGraph(value = "Comment.withoutAssociations")
    List<Comment> findAllByThreadIdAndParentCommentNull(int threadId);

    long countAllByThreadId(int threadId);

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

    @Query(value = """
      SELECT c
      FROM Comment c
      JOIN FETCH c.user u
      WHERE c.user.id = :userId
    """)
    Page<Comment> findCreatedByUserWithDetails(@Param("userId") int userId, Pageable pageable);
}
