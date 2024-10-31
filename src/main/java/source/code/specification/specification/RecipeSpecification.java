package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.pojo.FilterCriteria;

public class RecipeSpecification extends BaseSpecification<Recipe>{
  public RecipeSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Recipe> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return switch (criteria.getFilterKey()) {
      case Recipe.CATEGORY -> handleManyToManyProperty(root, Recipe.RECIPE_CATEGORY_ASSOCIATIONS,
              RecipeCategoryAssociation.RECIPE_CATEGORY, builder);
      default -> throw new IllegalStateException(
              "Unexpected value: " + criteria.getFilterKey());
    };
  }
}
