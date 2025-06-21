package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.thread.ForumThread;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer> {
    List<ForumThread> findAllByThreadCategoryId(int categoryId);
}
