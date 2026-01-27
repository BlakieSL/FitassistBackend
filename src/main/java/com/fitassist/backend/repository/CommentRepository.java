package com.fitassist.backend.repository;

import com.fitassist.backend.model.thread.Comment;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

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
	List<Object[]> findCommentHierarchy(int commentId);

	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM comment WHERE id = :id")
	void deleteCommentDirectly(int id);

	@Query(value = """
			WITH RECURSIVE ancestor_tree AS (
			    SELECT
			        c.id,
			        c.thread_id,
			        c.parent_comment_id
			    FROM comment c
			    WHERE c.id = :commentId

			    UNION ALL

			    SELECT
			        c.id,
			        c.thread_id,
			        c.parent_comment_id
			    FROM comment c
			    INNER JOIN ancestor_tree at ON c.id = at.parent_comment_id
			)
			SELECT
			    at.id,
			    at.thread_id
			FROM ancestor_tree at
			ORDER BY at.id
			""", nativeQuery = true)
	List<Object[]> findCommentAncestry(int commentId);

	@Query(value = """
			SELECT c.id
			FROM comment c
			LEFT JOIN user_comment uc ON uc.comment_id = c.id AND uc.type = 'LIKE'
			WHERE c.thread_id = :threadId
			AND c.parent_comment_id IS NULL
			GROUP BY c.id
			ORDER BY
			    CASE
			        WHEN :direction = 'ASC' THEN COUNT(uc.id)
			        ELSE -COUNT(uc.id)
			    END
			LIMIT :limit OFFSET :offset
			""", nativeQuery = true)
	List<Integer> findTopCommentIdsSortedByLikesCount(@Param("threadId") int threadId,
			@Param("direction") String direction, @Param("limit") int limit, @Param("offset") int offset);

	@EntityGraph(value = "Comment.summary")
	@Query("SELECT c FROM Comment c WHERE c.id IN :ids")
	List<Comment> findAllByIds(@Param("ids") List<Integer> ids);

	@Query(value = """
			SELECT COUNT(DISTINCT c.id)
			FROM comment c
			WHERE c.thread_id = :threadId
			AND c.parent_comment_id IS NULL
			""", nativeQuery = true)
	long countTopCommentsByThreadId(@Param("threadId") int threadId);

}
