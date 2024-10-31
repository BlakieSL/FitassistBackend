package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.ActivityField;
import source.code.model.Activity.Activity;
import source.code.pojo.FilterCriteria;

import java.util.Map;
import java.util.function.BiFunction;

public class ActivitySpecification extends BaseSpecification<Activity> {

  private final Map<String, BiFunction<Root<Activity>, CriteriaBuilder, Predicate>> fieldHandlers;

  public ActivitySpecification(@NonNull FilterCriteria criteria) {
    super(criteria);

    fieldHandlers = Map.of(
            ActivityField.CATEGORY.name(),
            (root, builder) -> handleEntityProperty(root, ActivityField.CATEGORY.getFieldName(), builder),

            ActivityField.MET.name(),
            (root, builder) -> handleNumericProperty(root.get(ActivityField.MET.getFieldName()), builder)
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Activity> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    BiFunction<Root<Activity>, CriteriaBuilder, Predicate> handler = fieldHandlers.get(criteria.getFilterKey());

    if (handler == null) {
      throw new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey());
    }

    return handler.apply(root, builder);
  }
}
