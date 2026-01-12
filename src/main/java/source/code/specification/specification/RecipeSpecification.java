package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.RecipeField;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeCategoryAssociation;
import source.code.model.recipe.RecipeFood;
import source.code.model.user.TypeOfInteraction;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

import static source.code.specification.SpecificationConstants.*;

public class RecipeSpecification extends AbstractSpecification<Recipe, RecipeField> {

	private static final String RECIPE_CATEGORY_ASSOCIATIONS_FIELD = "recipeCategoryAssociations";

	private static final String RECIPE_CATEGORY_FIELD = "recipeCategory";

	private static final String RECIPE_FOODS_FIELD = "recipeFoods";

	private static final String FOOD_FIELD = "food";

	public RecipeSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		super(criteria, dependencies);
	}

	@Override
	protected Class<RecipeField> getFieldClass() {
		return RecipeField.class;
	}

	@Override
	public Predicate toPredicate(@NotNull Root<Recipe> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
		Predicate visibilityPredicate = dependencies.getVisibilityPredicateBuilder()
			.buildVisibilityPredicate(builder, root, criteria, USER_FIELD, ID_FIELD, IS_PUBLIC_FIELD);

		if (criteria.getFilterKey() == null || criteria.getFilterKey().isEmpty()) {
			return visibilityPredicate;
		}

		RecipeField field = dependencies.getFieldResolver().resolveField(criteria, RecipeField.class);
		PredicateContext<Recipe> context = new PredicateContext<>(builder, root, query, criteria);
		Predicate fieldPredicate = buildPredicateForField(context, field);

		return builder.and(visibilityPredicate, fieldPredicate);
	}

	@Override
	protected Predicate buildPredicateForField(PredicateContext<Recipe> context, RecipeField field) {
		return switch (field) {
			case CATEGORY -> buildCategoryPredicate(context);
			case FOODS -> buildFoodsPredicate(context);
			case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(context, USER_FIELD);
			case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_RECIPES.getFieldName(), TYPE_FIELD, TypeOfInteraction.SAVE);
			case LIKED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_RECIPES.getFieldName(), TYPE_FIELD, TypeOfInteraction.LIKE);
			case DISLIKED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_RECIPES.getFieldName(), TYPE_FIELD, TypeOfInteraction.DISLIKE);
			case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
					LikesAndSaves.USER_RECIPES.getFieldName(), TYPE_FIELD, TypeOfInteraction.SAVE);
			case LIKE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
					LikesAndSaves.USER_RECIPES.getFieldName(), TYPE_FIELD, TypeOfInteraction.LIKE);
		};
	}

	private Predicate buildCategoryPredicate(PredicateContext<Recipe> context) {
		Join<Recipe, RecipeCategoryAssociation> categoryAssociationJoin = context.root()
			.join(RECIPE_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);

		return GenericSpecificationHelper.buildPredicateJoinProperty(context, categoryAssociationJoin,
				RECIPE_CATEGORY_FIELD);
	}

	private Predicate buildFoodsPredicate(PredicateContext<Recipe> context) {
		Join<Recipe, RecipeFood> recipeFoodsJoin = context.root().join(RECIPE_FOODS_FIELD, JoinType.LEFT);

		return GenericSpecificationHelper.buildPredicateJoinProperty(context, recipeFoodsJoin, FOOD_FIELD);
	}

}
