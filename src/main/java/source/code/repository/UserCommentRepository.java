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
            uc.comment.id as commentId,
            SUM(CASE WHEN uc.type = 'LIKE' THEN 1 ELSE 0 END) as likesCount,
            SUM(CASE WHEN uc.type = 'DISLIKE' THEN 1 ELSE 0 END) as dislikesCount
        FROM UserComment uc
        WHERE uc.comment.id IN :commentIds
        GROUP BY uc.comment.id
    """)
    List<CommentCountsProjection> findCountsByCommentIds(@Param("commentIds") List<Integer> commentIds);

    @Query(value = """
        SELECT uc
        FROM UserComment uc
        JOIN FETCH uc.comment c
        JOIN FETCH c.user
        WHERE uc.user.id = :userId
        AND uc.type = :type
    """)
    Page<UserComment> findByUserIdAndTypeWithComment(@Param("userId") int userId,
                                                      @Param("type") TypeOfInteraction type,
                                                      Pageable pageable);
}
