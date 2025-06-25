package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.RecipeField;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeCategoryAssociation;
import source.code.model.recipe.RecipeFood;
import source.code.model.user.TypeOfInteraction;

public class RecipeSpecification implements Specification<Recipe> {
    private final FilterCriteria criteria;

    public RecipeSpecification(@NonNull FilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(
            @NonNull Root<Recipe> root, CriteriaQuery<?> query,
            @NonNull CriteriaBuilder builder
    ) {
        RecipeField field;

        try {
            field = RecipeField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid filter key: " + criteria.getFilterKey());
        }

        return switch (field) {
            case RecipeField.CATEGORY -> GenericSpecificationHelper.buildPredicateJoinProperty(
                    builder,
                    criteria,
                    root,
                    RecipeField.CATEGORY.getFieldName(),
                    RecipeCategoryAssociation.RECIPE_CATEGORY
            );
            case RecipeField.FOODS -> GenericSpecificationHelper.buildPredicateJoinProperty(
                    builder,
                    criteria,
                    root,
                    RecipeField.FOODS.getFieldName(),
                    RecipeFood.FOOD
            );
            case RecipeField.SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    builder,
                    criteria,
                    root,
                    LikesAndSaves.USER_RECIPES.getFieldName(),
                    "type",
                    TypeOfInteraction.SAVE
            );
            case RecipeField.LIKE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    builder,
                    criteria,
                    root,
                    LikesAndSaves.USER_RECIPES.getFieldName(),
                    "type",
                    TypeOfInteraction.LIKE
            );
        };
    }

    public static RecipeSpecification of(FilterCriteria criteria) {
        return new RecipeSpecification(criteria);
    }
}
