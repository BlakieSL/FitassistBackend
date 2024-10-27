package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.Activity.Activity;
import source.code.model.Exercise.Exercise;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
  List<Activity> findAllByActivityCategory_Id(int categoryId);
  @EntityGraph(value = "Activity.withoutAssociations")
  @Query("SELECT a FROM Activity a")
  List<Activity> findAllWithoutAssociations();
}