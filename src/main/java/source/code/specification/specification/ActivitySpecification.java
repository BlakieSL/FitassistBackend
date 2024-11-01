package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.ActivityField;
import source.code.helper.Enum.Model.LikesAndSaves;
import source.code.helper.TriFunction;
import source.code.model.Activity.Activity;
import source.code.model.User.UserActivity;
import source.code.pojo.FilterCriteria;

import java.util.Map;
import java.util.Optional;

public class ActivitySpecification extends BaseSpecification<Activity> {

  private final Map<String, TriFunction<Root<Activity>, CriteriaQuery<?>, CriteriaBuilder, Predicate>> fieldHandlers;

  public ActivitySpecification(@NonNull FilterCriteria criteria) {
    super(criteria);

    fieldHandlers = Map.of(
            ActivityField.CATEGORY.name(),
            (root, query, builder) -> handleEntityProperty(root, ActivityField.CATEGORY.getFieldName(), builder),

            ActivityField.MET.name(),
            (root, query, builder) -> handleNumericProperty(root.get(ActivityField.MET.getFieldName()), builder),

            LikesAndSaves.LIKES.name(), this::handleLikesProperty,
            LikesAndSaves.SAVES.name(), this::handleSavesProperty
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Activity> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
            .map(handler -> handler.apply(root, query, builder))
            .orElseThrow(() -> new IllegalStateException(
                    "Unexpected filter key: " + criteria.getFilterKey()));
  }

  private Predicate handleLikesProperty(Root<Activity> root, CriteriaQuery<?> query,
                                        CriteriaBuilder builder) {
    return handleRangeProperty(root, query, builder, (short) 2);
  }

  private Predicate handleSavesProperty(Root<Activity> root, CriteriaQuery<?> query,
                                        CriteriaBuilder builder) {
    return handleRangeProperty(root, query, builder, (short) 1);
  }

  private Predicate handleRangeProperty(Root<Activity> root, CriteriaQuery<?> query,
                                        CriteriaBuilder builder, short typeValue) {

    Join<Activity, UserActivity> userActivityJoin = root.join("userActivities", JoinType.LEFT);

    Predicate typePredicate = builder.or(
            builder.isNull(userActivityJoin.get("type")),
            builder.equal(userActivityJoin.get("type"), typeValue)
    );

    query.groupBy(root.get("id"));

    Expression<Long> countExpression = builder.coalesce(builder.count(userActivityJoin.get("id")), 0L);
    query.having(createRangePredicate(countExpression, builder));

    return typePredicate;
  }


  private Predicate createRangePredicate(Expression<Long> countExpression, CriteriaBuilder builder) {
    Number value = (Number) criteria.getValue();
    Long longValue = value.longValue();
    return switch (criteria.getOperation()) {
      case GREATER_THAN -> builder.greaterThan(countExpression, longValue);
      case LESS_THAN -> builder.lessThan(countExpression, longValue);
      case EQUAL -> builder.equal(countExpression, longValue);
      case NOT_EQUAL -> builder.notEqual(countExpression, longValue);
      default -> throw new IllegalArgumentException("Unsupported operation: " + criteria.getOperation());
    };
  }
}
