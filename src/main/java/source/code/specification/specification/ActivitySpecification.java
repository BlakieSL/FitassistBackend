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
    return handleRangeProperty(root, query, builder, (short) 2, "userActivities");
  }

  private Predicate handleSavesProperty(Root<Activity> root, CriteriaQuery<?> query,
                                        CriteriaBuilder builder) {
    return handleRangeProperty(root, query, builder, (short) 1, "userActivities");
  }
}
