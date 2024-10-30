package source.code.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import source.code.pojo.FilterCriteria;

public class GenericSpecification<T> implements Specification<T> {

  private final FilterCriteria criteria;

  public GenericSpecification(FilterCriteria criteria) {
    this.criteria = criteria;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Path<?> path = criteria.getFilterKey().contains(".")
            ? getPathForNestedField(root, criteria.getFilterKey())
            : root.get(criteria.getFilterKey());

    return switch (criteria.getOperation()) {
      case GREATER_THAN -> builder.greaterThan(path.as(Double.class), (Double) criteria.getValue());
      case LESS_THAN -> builder.lessThan(path.as(Double.class), (Double) criteria.getValue());
      case EQUAL -> builder.equal(path, criteria.getValue());
      case NOT_EQUAL -> builder.notEqual(path, criteria.getValue());
      default -> null;
    };
  }

  private Path<?> getPathForNestedField(Root<T> root, String field) {
    String[] parts = field.split("\\.");
    Path<?> path = root;
    for (String part : parts) {
      path = path.get(part);
    }
    return path;
  }
}
