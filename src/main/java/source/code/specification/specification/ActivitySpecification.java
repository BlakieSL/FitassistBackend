package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import source.code.model.Activity.Activity;
import source.code.pojo.FilterCriteria;

public class ActivitySpecification implements Specification<Activity> {
  private final FilterCriteria criteria;

  public ActivitySpecification(FilterCriteria criteria) {
    this.criteria = criteria;
  }

  @Override
  public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    if ("categoryId".equals(criteria.getFilterKey())) {
      Join<Object, Object> categoryJoin = root.join("activityCategory");
      return switch (criteria.getOperation()) {
        case EQUAL -> builder.equal(categoryJoin.get("id"), criteria.getValue());
        case NOT_EQUAL -> builder.notEqual(categoryJoin.get("id"), criteria.getValue());
        default -> throw new IllegalArgumentException("Unsupported operation for categoryId: " + criteria.getOperation());
      };
    }

    if ("met".equals(criteria.getFilterKey())) {
      Path<Double> metPath = root.get("met");
      Double value = (Double) criteria.getValue();
      return switch (criteria.getOperation()) {
        case GREATER_THAN -> builder.greaterThan(metPath, value);
        case LESS_THAN -> builder.lessThan(metPath, value);
        case EQUAL -> builder.equal(metPath, value);
        case NOT_EQUAL -> builder.notEqual(metPath, value);
        default -> throw new IllegalArgumentException("Unsupported operation for met: " + criteria.getOperation());
      };
    }

    throw new IllegalArgumentException("Unsupported filter key: " + criteria.getFilterKey());
  }

}
