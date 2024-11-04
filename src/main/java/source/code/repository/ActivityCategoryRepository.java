package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Activity.ActivityCategory;

import java.util.Optional;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {
    Optional<ActivityCategory> findByName(String name);
}
