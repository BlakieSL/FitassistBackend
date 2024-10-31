package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.PlanField;
import source.code.model.Plan.Plan;
import source.code.model.Plan.PlanCategoryAssociation;
import source.code.pojo.FilterCriteria;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class PlanSpecification extends BaseSpecification<Plan>{
  private final Map<String, BiFunction<Root<Plan>, CriteriaBuilder, Predicate>> fieldHandlers;

  public PlanSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);

    fieldHandlers = Map.of(
            PlanField.TYPE.name(),
            (root, builder) -> handleEntityProperty(root, PlanField.TYPE.getFieldName(), builder),

            PlanField.DURATION.name(),
            (root, builder) -> handleNumericProperty(root.get(PlanField.DURATION.getFieldName()), builder),

            PlanField.EXPERTISE_LEVEL.name(),
            (root, builder) -> handleEntityProperty(root, PlanField.EXPERTISE_LEVEL.getFieldName(), builder),

            PlanField.CATEGORY.name(),
            (root, builder) -> handleManyToManyProperty(root, PlanField.CATEGORY.getFieldName(),
                    PlanCategoryAssociation.PLAN_CATEGORY, builder)
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Plan> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
            .map(handler -> handler.apply(root, builder))
            .orElseThrow(() -> new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey()));
  }
}
