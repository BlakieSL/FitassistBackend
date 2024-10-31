package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import source.code.pojo.FilterCriteria;

public abstract class BaseSpecification<T> implements Specification<T> {
  protected final FilterCriteria criteria;

  public BaseSpecification(FilterCriteria criteria) {
    this.criteria = criteria;
  }

  protected Predicate handleNumericProperty(Path<Double> path, CriteriaBuilder builder) {
    Double value = (Double) criteria.getValue();
    return switch (criteria.getOperation()) {
      case GREATER_THAN -> builder.greaterThan(path, value);
      case LESS_THAN -> builder.lessThan(path, value);
      case EQUAL -> builder.equal(path, value);
      case NOT_EQUAL -> builder.notEqual(path, value);
      default -> throw new IllegalArgumentException(
              "Unsupported operation for " + criteria.getFilterKey() + ": " + criteria.getOperation());
    };
  }

  protected Predicate handleEntityProperty(Root<T> root, String joinProperty,
                                           CriteriaBuilder builder) {
    Join<Object, Object> join = root.join(joinProperty);
    Object value = criteria.getValue();

    return switch (criteria.getOperation()) {
      case EQUAL -> builder.equal(join.get("id"), value);
      case NOT_EQUAL -> builder.notEqual(join.get("id"), value);
      default -> throw new IllegalArgumentException(
              "Unsupported operation for " + joinProperty + ": " + criteria.getOperation());
    };
  }

  protected Predicate handleManyToManyProperty(Root<T> root, String joinProperty,
                                               String joinProperty2,
                                               CriteriaBuilder builder) {

    Join<Object, Object> associationJoin = root.join(joinProperty);
    Object value = criteria.getValue();

    return switch (criteria.getOperation()) {
      case EQUAL -> builder.equal(associationJoin.get(joinProperty2).get("id"), value);
      case NOT_EQUAL -> builder.notEqual(associationJoin.get(joinProperty2).get("id"), value);
      default -> throw new IllegalArgumentException(
              "Unsupported operation for category: " + criteria.getOperation());
    };
  }
}
