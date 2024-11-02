package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;
import source.code.helper.Enum.Model.LikesAndSaves;
import source.code.helper.Enum.Model.PlanField;
import source.code.helper.TriFunction;
import source.code.model.Exercise.Exercise;
import source.code.model.Other.Equipment;
import source.code.model.Plan.Plan;
import source.code.model.Plan.PlanCategoryAssociation;
import source.code.model.Workout.Workout;
import source.code.model.Workout.WorkoutSet;
import source.code.dto.POJO.FilterCriteria;

import java.util.Map;
import java.util.Optional;

public class PlanSpecification extends BaseSpecification<Plan>{
  private final Map<String, TriFunction<Root<Plan>,
          CriteriaQuery<?>, CriteriaBuilder, Predicate>> fieldHandlers;

  public PlanSpecification(@NonNull FilterCriteria criteria) {
    super(criteria);

    fieldHandlers = Map.of(
            PlanField.TYPE.name(),
            (root, query, builder) ->
                    handleEntityProperty(root, PlanField.TYPE.getFieldName(), builder),

            PlanField.DURATION.name(),
            (root, query, builder) ->
                    handleNumericProperty(root.get(PlanField.DURATION.getFieldName()), builder),

            PlanField.EXPERTISE_LEVEL.name(),
            (root, query, builder) ->
                    handleEntityProperty(root, PlanField.EXPERTISE_LEVEL.getFieldName(), builder),

            PlanField.CATEGORY.name(),
            (root,query, builder) ->
                    handleManyToManyProperty(root, PlanField.CATEGORY.getFieldName(),
                    PlanCategoryAssociation.PLAN_CATEGORY, builder),

            PlanField.EQUIPMENT.name(),
            (root, query, builder) ->
                    handleEquipmentProperty(root, builder),

            LikesAndSaves.LIKES.name(),
            (root, query, builder) ->
                    handleLikesProperty(root, LikesAndSaves.USER_ACTIVITIES.getFieldName(), query, builder),

            LikesAndSaves.SAVES.name(),
            (root, query, builder) ->
                    handleLikesProperty(root, LikesAndSaves.USER_ACTIVITIES.getFieldName(), query, builder)
    );
  }

  @Override
  public Predicate toPredicate(@NonNull Root<Plan> root, @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder builder) {
    return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
            .map(handler -> handler.apply(root, query, builder))
            .orElseThrow(() -> new IllegalStateException(
                    "Unexpected filter key: " + criteria.getFilterKey()));
  }

  private Predicate handleEquipmentProperty(Root<Plan> root,  CriteriaBuilder builder) {
    Join<Plan, Workout> workoutJoin = root.join("workouts");
    Join<Workout, WorkoutSet> workoutSetJoin = workoutJoin.join("workoutSets");
    Join<WorkoutSet, Exercise> exerciseJoin = workoutSetJoin.join("exercise");
    Join<Exercise, Equipment> equipmentJoin = exerciseJoin.join("equipment");

    return switch (criteria.getOperation()) {
      case EQUAL -> builder.equal(equipmentJoin.get("id"), criteria.getValue());
      case NOT_EQUAL -> builder.notEqual(equipmentJoin.get("id"), criteria.getValue());
      default -> throw new IllegalStateException(
              "Unsupported operation: " + criteria.getOperation());
    };
  }
}
