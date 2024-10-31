package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.RecipeField;
import source.code.model.Recipe.Recipe;
import source.code.model.Recipe.RecipeCategoryAssociation;
import source.code.pojo.FilterCriteria;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class RecipeSpecification extends BaseSpecification<Recipe>{
  private final Map<String, BiFunction<Root<Recipe>, CriteriaBuilder, Predicate>> fieldHandlers;

  public RecipeSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);
    fieldHandlers = Map.of(
            RecipeField.CATEGORY.name(),
            (root, builder) -> handleManyToManyProperty(root, RecipeField.CATEGORY.getFieldName(),
                    RecipeCategoryAssociation.RECIPE_CATEGORY, builder)
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Recipe> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
            .map(handler -> handler.apply(root, builder))
            .orElseThrow(() -> new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey()));
  }
}
