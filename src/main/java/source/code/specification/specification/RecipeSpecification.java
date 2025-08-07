package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.RecipeField;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeCategoryAssociation;
import source.code.model.recipe.RecipeFood;
import source.code.model.user.TypeOfInteraction;

public class RecipeSpecification implements Specification<Recipe> {

    private static final String RECIPE_CATEGORY_ASSOCIATIONS_FIELD = "recipeCategoryAssociations";
    private static final String RECIPE_CATEGORY_FIELD = "recipeCategory";
    private static final String USER_FIELD = "user";
    private static final String IS_PUBLIC_FIELD = "isPublic";
    private static final String RECIPE_FOODS_FIELD = "recipeFoods";
    private static final String FOOD_FIELD = "food";
    private static final String TYPE_FIELD = "type";
    private static final String ID_FIELD = "id";

    private final transient FilterCriteria criteria;

    public RecipeSpecification(@NonNull FilterCriteria criteria) {
        this.criteria = criteria;
    }

    public static RecipeSpecification of(@NonNull FilterCriteria criteria) {
        return new RecipeSpecification(criteria);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Recipe> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        initializeFetches(root);
        Predicate publicPredicate = buildPublicPredicate(root, builder);
        RecipeField field = resolveRecipeField(criteria);

        Predicate fieldPredicate = buildPredicateForField(builder, root, field);
        return builder.and(publicPredicate, fieldPredicate);
    }

    private void initializeFetches(Root<Recipe> root) {
        Fetch<Recipe, RecipeCategoryAssociation> categoryAssociationsFetch =
                root.fetch(RECIPE_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);
        categoryAssociationsFetch.fetch(RECIPE_CATEGORY_FIELD, JoinType.LEFT);
    }

    private Predicate buildPublicPredicate(Root<Recipe> root, CriteriaBuilder builder) {
        if (criteria.getIsPublic() != null && !criteria.getIsPublic()) {
            return builder.equal(
                    root.get(USER_FIELD).get(ID_FIELD),
                    AuthorizationUtil.getUserId()
            );
        }
        return builder.isTrue(root.get(IS_PUBLIC_FIELD));
    }

    private RecipeField resolveRecipeField(FilterCriteria criteria) {
        try {
            return RecipeField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }
    }

    private Predicate buildPredicateForField(
            CriteriaBuilder builder, Root<Recipe> root, RecipeField field) {

        return switch (field) {
            case CATEGORY -> buildCategoryPredicate(builder, root);
            case FOODS -> buildFoodsPredicate(builder, root);
            case SAVE -> buildInteractionPredicate(builder, root, TypeOfInteraction.SAVE);
            case LIKE -> buildInteractionPredicate(builder, root, TypeOfInteraction.LIKE);
        };
    }

    private Predicate buildCategoryPredicate(CriteriaBuilder builder, Root<Recipe> root) {
        Join<Recipe, RecipeCategoryAssociation> categoryAssociationJoin =
                root.join(RECIPE_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);

        return GenericSpecificationHelper.buildPredicateJoinProperty(
                builder, criteria, categoryAssociationJoin, RECIPE_CATEGORY_FIELD);
    }

    private Predicate buildFoodsPredicate(CriteriaBuilder builder, Root<Recipe> root) {
        Join<Recipe, RecipeFood> recipeFoodsJoin =
                root.join(RECIPE_FOODS_FIELD, JoinType.LEFT);

        return GenericSpecificationHelper.buildPredicateJoinProperty(
                builder, criteria, recipeFoodsJoin, FOOD_FIELD);
    }

    private Predicate buildInteractionPredicate(
            CriteriaBuilder builder, Root<Recipe> root, TypeOfInteraction interactionType) {

        return GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                builder, criteria, root,
                LikesAndSaves.USER_RECIPES.getFieldName(),
                TYPE_FIELD, interactionType);
    }
}