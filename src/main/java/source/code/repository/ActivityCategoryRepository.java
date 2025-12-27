package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.activity.ActivityCategory;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {

	boolean existsByIdAndActivitiesIsNotEmpty(Integer id);

}
