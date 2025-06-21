package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserThread;

import java.util.List;
import java.util.Optional;

public interface UserThreadRepository
        extends JpaRepository<UserThread, Integer>
{
    boolean existsByUserIdAndForumThreadId(int userId, int forumThreadId);
    Optional<UserThread> findByUserIdAndForumThreadId(int userId, int forumThreadId);
    List<UserThread> findAllByUserId(int userId);
}
