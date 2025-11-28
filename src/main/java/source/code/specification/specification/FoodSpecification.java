package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.FoodField;
import source.code.model.food.Food;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

import java.math.BigDecimal;

@AllArgsConstructor(staticName = "of")
public class FoodSpecification implements Specification<Food> {
    private static final String FOOD_CATEGORY_FIELD = "foodCategory";
    private static final String CALORIES_FIELD = "calories";
    private static final String PROTEIN_FIELD = "protein";
    private static final String FAT_FIELD = "fat";
    private static final String CARBOHYDRATES_FIELD = "carbohydrates";

    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    @Override
    public Predicate toPredicate(@NonNull Root<Food> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        dependencies.getFetchInitializer().initializeFetches(root, query, FOOD_CATEGORY_FIELD);

        FoodField field = dependencies.getFieldResolver().resolveField(criteria, FoodField.class);

        return buildPredicateForField(builder, criteria, root, field, query);
    }

    private Predicate buildPredicateForField(CriteriaBuilder builder,
                                             FilterCriteria criteria,
                                             Root<Food> root,
                                             FoodField field,
                                             CriteriaQuery<?> query) {
        return switch (field) {
            case CALORIES -> buildNumericPredicate(builder, criteria, root.get(CALORIES_FIELD));
            case PROTEIN -> buildNumericPredicate(builder, criteria, root.get(PROTEIN_FIELD));
            case FAT -> buildNumericPredicate(builder, criteria, root.get(FAT_FIELD));
            case CARBOHYDRATES -> buildNumericPredicate(builder, criteria, root.get(CARBOHYDRATES_FIELD));
            case CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder, criteria, root, FOOD_CATEGORY_FIELD);
            case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    builder, criteria, root, LikesAndSaves.USER_FOODS.getFieldName());
            case SAVE -> {
                query.groupBy(root.get("id"));
                yield GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                        builder, criteria, root, LikesAndSaves.USER_FOODS.getFieldName(), null, null);
            }
        };
    }

    private Predicate buildNumericPredicate(CriteriaBuilder builder, FilterCriteria criteria, Path<BigDecimal> path) {
        return GenericSpecificationHelper.buildPredicateNumericProperty(builder, criteria, path);
    }
}