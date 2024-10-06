package source.code.repository;

import source.code.model.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {
    Optional<ActivityCategory> findByName(String name);
}
