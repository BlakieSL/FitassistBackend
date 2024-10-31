package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.model.Exercise.Exercise;
import source.code.model.Exercise.ExerciseCategoryAssociation;
import source.code.pojo.FilterCriteria;

public class ExerciseSpecification extends BaseSpecification<Exercise>{
  public ExerciseSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);
  }
  @Override
  public Predicate toPredicate(@NonNull Root<Exercise> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return switch (criteria.getFilterKey()) {
      case Exercise.EXPERTISE_LEVEL -> 
              handleEntityProperty(root, Exercise.EXPERTISE_LEVEL, "id", builder);
      case Exercise.MECHANICS_TYPE -> 
              handleEntityProperty(root, Exercise.MECHANICS_TYPE, "id", builder);
      case Exercise.FORCE_TYPE -> 
              handleEntityProperty(root, Exercise.FORCE_TYPE, "id", builder);
      case Exercise.EXERCISE_EQUIPMENT -> 
              handleEntityProperty(root, Exercise.EXERCISE_EQUIPMENT, "id", builder);
      case Exercise.EXERCISE_TYPE ->
              handleEntityProperty(root, Exercise.EXERCISE_TYPE, "id", builder);
      case Exercise.CATEGORY ->
              handleManyToManyProperty(root, Exercise.EXERCISE_CATEGORY_ASSOCIATIONS,
                      ExerciseCategoryAssociation.EXERCISE_CATEGORY, "id", builder);
      default -> throw new IllegalStateException("Unexpected value: " + criteria.getFilterKey());
    };
  }
}
