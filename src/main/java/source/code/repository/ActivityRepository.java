package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.Activity.Activity;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {
  List<Activity> findAllByActivityCategory_Id(int categoryId);

  @EntityGraph(value = "Activity.withoutAssociations")
  @Query("SELECT a FROM Activity a")
  List<Activity> findAllWithoutAssociations();
}