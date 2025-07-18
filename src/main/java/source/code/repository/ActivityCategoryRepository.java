package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import source.code.model.activity.ActivityCategory;

import java.util.Optional;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {
    boolean existsByIdAndActivitiesIsNotEmpty(Integer id);
}
