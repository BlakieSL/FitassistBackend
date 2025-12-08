package source.code.specification.specification;

import jakarta.persistence.criteria.Predicate;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.FoodField;
import source.code.model.food.Food;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

import static source.code.specification.SpecificationConstants.ID_FIELD;

public class FoodSpecification extends AbstractSpecification<Food, FoodField> {

    private static final String FOOD_CATEGORY_FIELD = "foodCategory";
    private static final String CALORIES_FIELD = "calories";
    private static final String PROTEIN_FIELD = "protein";
    private static final String FAT_FIELD = "fat";
    private static final String CARBOHYDRATES_FIELD = "carbohydrates";

    public FoodSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
        super(criteria, dependencies);
    }

    @Override
    protected Class<FoodField> getFieldClass() {
        return FoodField.class;
    }

    @Override
    protected Predicate buildPredicateForField(PredicateContext<Food> context, FoodField field) {
        return switch (field) {
            case CALORIES -> GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(CALORIES_FIELD));
            case PROTEIN -> GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(PROTEIN_FIELD));
            case FAT -> GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(FAT_FIELD));
            case CARBOHYDRATES -> GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(CARBOHYDRATES_FIELD));
            case CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(context, FOOD_CATEGORY_FIELD);
            case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    context, LikesAndSaves.USER_FOODS.getFieldName());
            case SAVE -> {
                context.query().groupBy(context.root().get(ID_FIELD));
                yield GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                        context, LikesAndSaves.USER_FOODS.getFieldName(), null, null);
            }
        };
    }
}
