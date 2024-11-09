package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.field.FoodField;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.TriFunction;
import source.code.model.food.Food;

import java.util.Map;
import java.util.Optional;

public class FoodSpecification extends BaseSpecification<Food> {

    private final Map<String, TriFunction<Root<Food>,
            CriteriaQuery<?>, CriteriaBuilder, Predicate>> fieldHandlers;

    public FoodSpecification(@NonNull FilterCriteria criteria) {
        super(criteria);

        fieldHandlers = Map.of(
                FoodField.CALORIES.name(),
                (root, query, builder) -> handleNumericProperty(
                        root.get(FoodField.CALORIES.getFieldName()),
                        builder
                ),
                FoodField.PROTEIN.name(),
                (root, query, builder) -> handleNumericProperty(
                        root.get(FoodField.PROTEIN.getFieldName()),
                        builder
                ),
                FoodField.FAT.name(),
                (root, query, builder) -> handleNumericProperty(
                        root.get(FoodField.FAT.getFieldName()),
                        builder
                ),
                FoodField.CARBOHYDRATES.name(),
                (root, query, builder) -> handleNumericProperty(
                        root.get(FoodField.CARBOHYDRATES.getFieldName()),
                        builder
                ),
                FoodField.CATEGORY.name(),
                (root, query, builder) -> handleEntityProperty(
                        root,
                        FoodField.CATEGORY.getFieldName(),
                        builder
                ),
                LikesAndSaves.LIKES.name(),
                (root, query, builder) -> handleLikesProperty(
                        root,
                        LikesAndSaves.USER_FOODS.getFieldName(),
                        query,
                        builder
                ),
                LikesAndSaves.SAVES.name(),
                (root, query, builder) -> handleSavesProperty(
                        root,
                        LikesAndSaves.USER_FOODS.getFieldName(),
                        query,
                        builder
                )

        );
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Food> root, @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
                .map(handler -> handler.apply(root, query, builder))
                .orElseThrow(() -> new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey()));
    }

    public static FoodSpecification of(FilterCriteria criteria) {
        return new FoodSpecification(criteria);
    }
}
