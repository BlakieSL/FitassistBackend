package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.ExerciseField;
import source.code.model.Exercise.Exercise;
import source.code.model.Exercise.ExerciseCategoryAssociation;
import source.code.pojo.FilterCriteria;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class ExerciseSpecification extends BaseSpecification<Exercise>{
  private final Map<String, BiFunction<Root<Exercise>, CriteriaBuilder, Predicate>> fieldHandlers;

  public ExerciseSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);

    fieldHandlers = Map.of(
            ExerciseField.EXPERTISE_LEVEL.name(),
            (root, builder) -> handleEntityProperty(root, ExerciseField.EXPERTISE_LEVEL.getFieldName(), builder),

            ExerciseField.EQUIPMENT.name(),
            (root, builder) -> handleEntityProperty(root, ExerciseField.EQUIPMENT.getFieldName(), builder),

            ExerciseField.TYPE.name(),
            (root, builder) -> handleEntityProperty(root, ExerciseField.TYPE.getFieldName(), builder),

            ExerciseField.MECHANICS_TYPE.name(),
            (root, builder) -> handleEntityProperty(root, ExerciseField.MECHANICS_TYPE.getFieldName(), builder),

            ExerciseField.FORCE_TYPE.name(),
            (root, builder) -> handleEntityProperty(root, ExerciseField.FORCE_TYPE.getFieldName(), builder),

            ExerciseField.CATEGORY.name(),
            (root, builder) -> handleManyToManyProperty(root, ExerciseField.CATEGORY.getFieldName(),
                    ExerciseCategoryAssociation.EXERCISE_CATEGORY, builder)
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Exercise> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
            .map(handler -> handler.apply(root, builder))
            .orElseThrow(() -> new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey()));
  }
}
