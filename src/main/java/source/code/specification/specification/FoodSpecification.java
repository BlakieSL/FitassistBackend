package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.FoodField;
import source.code.model.food.Food;

import java.math.BigDecimal;

public class FoodSpecification implements Specification<Food> {
    private static final String FOOD_CATEGORY_FIELD = "foodCategory";
    private static final String CALORIES_FIELD = "calories";
    private static final String PROTEIN_FIELD = "protein";
    private static final String FAT_FIELD = "fat";
    private static final String CARBOHYDRATES_FIELD = "carbohydrates";

    private final FilterCriteria criteria;

    public FoodSpecification(@NonNull FilterCriteria criteria) {
        this.criteria = criteria;
    }

    public static FoodSpecification of(@NonNull FilterCriteria criteria) {
        return new FoodSpecification(criteria);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Food> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        initializeFetches(root);
        return buildPredicateForField(builder, criteria, root, resolveFoodField(criteria), query);
    }

    private void initializeFetches(Root<Food> root) {
        root.fetch(FOOD_CATEGORY_FIELD, JoinType.LEFT);
    }

    private FoodField resolveFoodField(FilterCriteria criteria) {
        try {
            return FoodField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }
    }

    private Predicate buildPredicateForField(
            CriteriaBuilder builder,
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
                    builder,
                    criteria,
                    root,
                    FOOD_CATEGORY_FIELD
            );
            case SAVE -> {
                Predicate predicate = GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                        builder,
                        criteria,
                        root,
                        LikesAndSaves.USER_FOODS.getFieldName(),
                        null,
                        null
                );

                query.groupBy(root.get("id"));

                yield predicate;
            }
        };
    }

    private Predicate buildNumericPredicate(CriteriaBuilder builder, FilterCriteria criteria, Path<BigDecimal> path) {
        return GenericSpecificationHelper.buildPredicateNumericProperty(builder, criteria, path);
    }
}