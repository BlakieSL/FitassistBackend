package source.code.repository;

import source.code.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    List<Activity> findAllByActivityCategory_Id(int categoryId);
    List<Activity> findAllByNameContainingIgnoreCase(String name);
}