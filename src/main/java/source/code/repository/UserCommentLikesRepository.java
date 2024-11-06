package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserCommentLikes;

public interface UserCommentLikesRepository extends JpaRepository<UserCommentLikes, Integer> {
}
