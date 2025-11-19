package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.recipe.RecipeCategoryAssociation;

import java.util.List;

public interface RecipeCategoryAssociationRepository extends JpaRepository<RecipeCategoryAssociation, Integer> {
    List<RecipeCategoryAssociation> findByRecipeCategoryId(int categoryId);

    @Query("""
        SELECT rca.recipe.id, rc.id, rc.name
        FROM RecipeCategoryAssociation rca
        JOIN rca.recipeCategory rc
        WHERE rca.recipe.id IN :recipeIds
        """)
    List<Object[]> findCategoryDataByRecipeIds(@Param("recipeIds") List<Integer> recipeIds);
}