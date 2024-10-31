package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;
import source.code.model.Food.Food;
import source.code.pojo.FilterCriteria;

public class FoodSpecification extends BaseSpecification<Food>{
  public FoodSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Food> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return switch (criteria.getFilterKey()) {
      case Food.CALORIES -> handleNumericProperty(root.get(Food.CALORIES), builder);
      case Food.PROTEIN -> handleNumericProperty(root.get(Food.PROTEIN), builder);
      case Food.FAT -> handleNumericProperty(root.get(Food.FAT), builder);
      case Food.CARBOHYDRATES -> handleNumericProperty(root.get(Food.CARBOHYDRATES), builder);
      case Food.CATEGORY -> handleEntityProperty(root, Food.CATEGORY, "id", builder);
      default -> throw new IllegalStateException("Unexpected value: " + criteria.getFilterKey());
    };
  }
}
