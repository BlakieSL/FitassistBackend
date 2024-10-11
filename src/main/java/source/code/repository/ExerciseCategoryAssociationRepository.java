package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.ExerciseCategoryAssociation;

import java.util.List;

public interface ExerciseCategoryAssociationRepository extends JpaRepository<ExerciseCategoryAssociation, Integer> {
  List<ExerciseCategoryAssociation> findByExerciseCategoryId(int categoryId);
}