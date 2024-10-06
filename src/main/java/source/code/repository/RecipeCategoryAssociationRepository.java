package source.code.repository;

import source.code.model.RecipeCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCategoryAssociationRepository extends JpaRepository<RecipeCategoryAssociation, Integer> {
    List<RecipeCategoryAssociation> findByRecipeCategoryId(int categoryId);
}