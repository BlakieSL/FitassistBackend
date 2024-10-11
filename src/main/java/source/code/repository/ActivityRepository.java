package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Activity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
  List<Activity> findAllByActivityCategory_Id(int categoryId);

  List<Activity> findAllByNameContainingIgnoreCase(String name);
}