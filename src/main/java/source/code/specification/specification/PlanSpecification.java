package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.model.Plan.Plan;
import source.code.model.Plan.PlanCategoryAssociation;
import source.code.pojo.FilterCriteria;

public class PlanSpecification extends BaseSpecification<Plan>{
  public PlanSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Plan> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return switch (criteria.getFilterKey()) {
      case Plan.PLAN_TYPE ->
              handleEntityProperty(root, Plan.PLAN_TYPE, "id", builder);
      case Plan.PLAN_DURATION ->
              handleNumericProperty(root.get(Plan.PLAN_DURATION), builder);
      case Plan.PLAN_EQUIPMENT ->
              handleEntityProperty(root, Plan.PLAN_EQUIPMENT, "id", builder);
      case Plan.PLAN_EXPERTISE_LEVEL ->
              handleEntityProperty(root, Plan.PLAN_EXPERTISE_LEVEL, "id", builder);
      case Plan.CATEGORY -> handleManyToManyProperty(root, Plan.PLAN_CATEGORY_ASSOCIATIONS,
              PlanCategoryAssociation.PLAN_CATEGORY, "id", builder);
      default -> throw new IllegalStateException("Unexpected value: " + criteria.getFilterKey());
    };
  }
}
