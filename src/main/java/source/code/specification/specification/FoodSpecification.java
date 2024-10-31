package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.FoodField;
import source.code.model.Food.Food;
import source.code.pojo.FilterCriteria;

import java.util.Map;
import java.util.function.BiFunction;

public class FoodSpecification extends BaseSpecification<Food>{

  private final Map<String, BiFunction<Root<Food>, CriteriaBuilder, Predicate>> fieldHandlers;

  public FoodSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);

    fieldHandlers = Map.of(
            FoodField.CALORIES.name(),
            (root, builder) -> handleNumericProperty(root.get(FoodField.CALORIES.getFieldName()), builder),

            FoodField.PROTEIN.name(),
            (root, builder) -> handleNumericProperty(root.get(FoodField.PROTEIN.getFieldName()), builder),

            FoodField.FAT.name(),
            (root, builder) -> handleNumericProperty(root.get(FoodField.FAT.getFieldName()), builder),

            FoodField.CARBOHYDRATES.name(),
            (root, builder) -> handleNumericProperty(root.get(FoodField.CARBOHYDRATES.getFieldName()), builder),

            FoodField.CATEGORY.name(),
            (root, builder) -> handleEntityProperty(root, FoodField.CATEGORY.getFieldName(), builder)
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Food> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    BiFunction<Root<Food>, CriteriaBuilder, Predicate> handler = fieldHandlers.get(criteria.getFilterKey());

    if (handler == null) {
      throw new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey());
    }

    return handler.apply(root, builder);
  }
}
