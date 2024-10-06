package source.code.repository;

import source.code.model.ExerciseCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseCategoryAssociationRepository extends JpaRepository<ExerciseCategoryAssociation, Integer> {
    List<ExerciseCategoryAssociation> findByExerciseCategoryId(int categoryId);
}