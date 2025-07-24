package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserComment;

import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;

public interface UserCommentRepository extends JpaRepository<UserComment, Integer> {
    boolean existsByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);
    Optional<UserComment> findByUserIdAndCommentIdAndType(int userId, int commentId, TypeOfInteraction type);
    List<UserComment> findByUserIdAndType(int userId, TypeOfInteraction type);
    long countByCommentIdAndType(int commentId, TypeOfInteraction type);

}
