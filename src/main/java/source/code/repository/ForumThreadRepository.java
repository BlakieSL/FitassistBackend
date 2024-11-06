package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.forum.ForumThread;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer> {
}
