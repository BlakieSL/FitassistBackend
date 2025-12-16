package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import source.code.model.thread.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    @EntityGraph(value = "Comment.summary")
    @NotNull
    @Override
    Page<Comment> findAll(Specification<Comment> spec, @NotNull Pageable pageable);

    @EntityGraph(value = "Comment.summary")
    @NotNull
    @Override
    Optional<Comment> findById(@NotNull Integer id);

    @EntityGraph(value = "Comment.summary")
    Page<Comment> findAllByThreadIdAndParentCommentNull(int threadId, Pageable pageable);

    @Query(value = """
            WITH RECURSIVE comment_tree AS (
                SELECT
                    c.id,
                    c.text,
                    c.thread_id,
                    c.user_id,
                    c.parent_comment_id,
                    c.created_at
                FROM comment c
                WHERE c.parent_comment_id = :commentId
            
                UNION ALL
            
                SELECT
                    c.id,
                    c.text,
                    c.thread_id,
                    c.user_id,
                    c.parent_comment_id,
                    c.created_at
                FROM comment c
                INNER JOIN comment_tree ct ON c.parent_comment_id = ct.id
            )
            SELECT
                ct.id,
                ct.text,
                ct.thread_id,
                ct.user_id,
                ct.parent_comment_id,
                ct.created_at,
                u.username
            FROM comment_tree ct
            JOIN user u ON u.id = ct.user_id
            ORDER BY ct.id
            """, nativeQuery = true)
    List<Object[]> findCommentHierarchy(Integer commentId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM comment WHERE id = :id")
    void deleteCommentDirectly(int id);
}
