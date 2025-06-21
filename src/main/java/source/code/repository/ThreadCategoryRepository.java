package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.thread.ThreadCategory;

public interface ThreadCategoryRepository extends JpaRepository<ThreadCategory, Integer> {
}