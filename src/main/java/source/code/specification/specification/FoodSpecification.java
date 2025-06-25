package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.field.FoodField;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.model.food.Food;

public class FoodSpecification implements Specification<Food> {
    private final FilterCriteria criteria;

    public FoodSpecification(FilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Food> root, CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        FoodField field;
        try {
            field = FoodField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }

        return switch (field) {
            case FoodField.CALORIES -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder,
                    criteria,
                    root.get(FoodField.CALORIES.getFieldName())
            );
            case FoodField.PROTEIN -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder,
                    criteria,
                    root.get(FoodField.PROTEIN.getFieldName())
            );
            case FoodField.FAT -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder,
                    criteria,
                    root.get(FoodField.FAT.getFieldName())
            );
            case FoodField.CARBOHYDRATES -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder,
                    criteria,
                    root.get(FoodField.CARBOHYDRATES.getFieldName())
            );
            case FoodField.CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    FoodField.CATEGORY.getFieldName()
            );
            case FoodField.SAVE -> {
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

    public static FoodSpecification of(FilterCriteria criteria) {
        return new FoodSpecification(criteria);
    }
}
