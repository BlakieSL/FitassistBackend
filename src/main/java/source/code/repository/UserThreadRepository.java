package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.user.UserThread;

import java.util.List;
import java.util.Optional;

public interface UserThreadRepository
        extends JpaRepository<UserThread, Integer>
{
    boolean existsByUserIdAndForumThreadId(int userId, int forumThreadId);
    Optional<UserThread> findByUserIdAndForumThreadId(int userId, int forumThreadId);

    @Query("""
           SELECT ut FROM UserThread ut
           JOIN FETCH ut.forumThread ft
           JOIN FETCH ft.user
           LEFT JOIN FETCH ft.userThreads
           LEFT JOIN FETCH ft.comments
           WHERE ut.user.id = :userId
           """)
    List<UserThread> findAllByUserId(@Param("userId") int userId);
}
