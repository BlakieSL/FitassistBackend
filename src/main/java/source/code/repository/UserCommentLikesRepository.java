package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserCommentLikes;

import java.util.List;
import java.util.Optional;

public interface UserCommentLikesRepository extends JpaRepository<UserCommentLikes, Integer> {
    boolean existsByUserIdAndCommentId(int userId, int commentId);
    Optional<UserCommentLikes> findByUserIdAndCommentId(int userId, int commentId);
    List<UserCommentLikes> findAllByUserId(int userId);
    long countAllByCommentId(int commentId);
}
