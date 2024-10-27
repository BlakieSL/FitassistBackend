package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.Exercise.Exercise;
import source.code.model.Plan.Plan;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
  @EntityGraph(value = "Plan.withoutAssociations")
  @Query("SELECT p FROM Plan p")
  List<Plan> findAllWithoutAssociations();
}