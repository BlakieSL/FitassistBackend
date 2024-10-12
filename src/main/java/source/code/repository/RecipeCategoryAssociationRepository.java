package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Recipe.RecipeCategoryAssociation;

import java.util.List;

public interface RecipeCategoryAssociationRepository extends JpaRepository<RecipeCategoryAssociation, Integer> {
  List<RecipeCategoryAssociation> findByRecipeCategoryId(int categoryId);
}