package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;
import source.code.model.Activity.Activity;
import source.code.pojo.FilterCriteria;

public class ActivitySpecification extends BaseSpecification<Activity> {

  public ActivitySpecification(@NonNull FilterCriteria criteria) {
    super(criteria);
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Activity> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return switch (criteria.getFilterKey()) {
      case Activity.CATEGORY -> handleEntityProperty(root, Activity.CATEGORY, "id", builder);
      case Activity.MET -> handleNumericProperty(root.get(Activity.MET), builder);
      default -> throw new IllegalStateException("Unexpected value: " + criteria.getFilterKey());
    };
  }
}
