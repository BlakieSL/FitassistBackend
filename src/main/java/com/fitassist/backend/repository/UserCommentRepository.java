package com.fitassist.backend.repository;

import com.fitassist.backend.dto.pojo.projection.comment.CommentCountsProjection;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import com.fitassist.backend.model.user.interactions.UserComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCommentRepository extends JpaRepository<UserComment, Integer> {

	boolean existsByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);

	Optional<UserComment> findByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);

	long countByCommentIdAndType(int commentId, TypeOfInteraction type);

	@Query(value = """
			    WITH RECURSIVE comment_descendants AS (
			        SELECT id, parent_comment_id, parent_comment_id as root_comment_id
			        FROM comment
			        WHERE parent_comment_id IN :commentIds

			        UNION ALL

			        SELECT c.id, c.parent_comment_id, cd.root_comment_id
			        FROM comment c
			        INNER JOIN comment_descendants cd ON c.parent_comment_id = cd.id
			    ),
			    replies_counts AS (
			        SELECT root_comment_id as comment_id, COUNT(*) as reply_count
			        FROM comment_descendants
			        GROUP BY root_comment_id
			    )
			    SELECT
			        c.id as commentId,
			        COALESCE(SUM(CASE WHEN uc.type = 'LIKE' THEN 1 ELSE 0 END), 0) as likesCount,
			        COALESCE(SUM(CASE WHEN uc.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) as dislikesCount,
			        MAX(CASE WHEN uc.type = 'LIKE' AND uc.user_id = :userId THEN 1 ELSE 0 END) as isLiked,
			        MAX(CASE WHEN uc.type = 'DISLIKE' AND uc.user_id = :userId THEN 1 ELSE 0 END) as isDisliked,
			        COALESCE(rc.reply_count, 0) as repliesCount
			    FROM comment c
			    LEFT JOIN user_comment uc ON uc.comment_id = c.id
			    LEFT JOIN replies_counts rc ON rc.comment_id = c.id
			    WHERE c.id IN :commentIds
			    GROUP BY c.id, rc.reply_count
			""", nativeQuery = true)
	List<CommentCountsProjection> findCountsAndInteractionsByCommentIds(@Param("userId") int userId,
			@Param("commentIds") List<Integer> commentIds);

	@Query(value = """
			    SELECT uc
			    FROM UserComment uc
			    JOIN FETCH uc.comment c
			    JOIN FETCH c.thread t
			    JOIN FETCH c.user
			    WHERE uc.user.id = :userId
			    AND uc.type = :type
			""")
	Page<UserComment> findAllByUserIdAndType(@Param("userId") int userId, @Param("type") TypeOfInteraction type,
			Pageable pageable);

}
