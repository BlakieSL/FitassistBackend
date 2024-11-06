package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.forum.ThreadCategory;

public interface ThreadCategoryRepository extends JpaRepository<ThreadCategory, Integer> {
}