package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserComment;

import java.util.List;
import java.util.Optional;

public interface UserCommentRepository extends JpaRepository<UserComment, Integer> {
    boolean existsByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);
    Optional<UserComment> findByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);
    List<UserComment> findByUserIdAndType(int userId, TypeOfInteraction type);
    long countByCommentIdAndType(int commentId, TypeOfInteraction type);

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
               CAST((SELECT COUNT(uc2) FROM UserComment uc2 WHERE uc2.comment.id = c.id AND uc2.type = 'LIKE') -
                    (SELECT COUNT(uc3) FROM UserComment uc3 WHERE uc3.comment.id = c.id AND uc3.type = 'DISLIKE') AS int),
               CAST((SELECT COUNT(cr) FROM Comment cr WHERE cr.parentComment.id = c.id) AS int),
               uc.createdAt)
           FROM UserComment uc
           JOIN uc.comment c
           JOIN c.user u
           WHERE uc.user.id = :userId
           AND uc.type = :type
           ORDER BY uc.createdAt DESC
           """)
    List<CommentSummaryDto> findCommentSummaryByUserIdAndType(@Param("userId") int userId, @Param("type") TypeOfInteraction type);

}
