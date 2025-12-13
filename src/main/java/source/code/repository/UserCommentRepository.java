package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.comment.CommentCountsProjection;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserComment;

import java.util.List;
import java.util.Optional;

public interface UserCommentRepository extends JpaRepository<UserComment, Integer> {
    boolean existsByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);

    Optional<UserComment> findByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);

    @Query("""
        SELECT
            c.id as commentId,
            COALESCE(SUM(CASE WHEN uc.type = 'LIKE' THEN 1 ELSE 0 END), 0) as likesCount,
            COALESCE(SUM(CASE WHEN uc.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) as dislikesCount,
            MAX(CASE WHEN uc.type = 'LIKE' AND uc.user.id = :userId THEN 1 ELSE 0 END) as isLiked,
            MAX(CASE WHEN uc.type = 'DISLIKE' AND uc.user.id = :userId THEN 1 ELSE 0 END) as isDisliked,
            (SELECT COUNT(r) FROM Comment r WHERE r.parentComment.id = c.id) as repliesCount
        FROM Comment c
        LEFT JOIN UserComment uc ON uc.comment.id = c.id
        WHERE c.id IN :commentIds
        GROUP BY c.id
    """)
    List<CommentCountsProjection> findCountsAndInteractionsByCommentIds(@Param("userId") int userId,
                                                                        @Param("commentIds") List<Integer> commentIds);

    @Query(value = """
        SELECT uc
        FROM UserComment uc
        JOIN FETCH uc.comment c
        JOIN FETCH c.user
        WHERE uc.user.id = :userId
        AND uc.type = :type
    """)
    Page<UserComment> findAllByUserIdAndType(@Param("userId") int userId,
                                             @Param("type") TypeOfInteraction type,
                                             Pageable pageable);
}
