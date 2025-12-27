package source.code.specification.specification;

import static source.code.specification.SpecificationConstants.TYPE_FIELD;
import static source.code.specification.SpecificationConstants.USER_FIELD;

import jakarta.persistence.criteria.Predicate;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ForumThreadField;
import source.code.model.thread.ForumThread;
import source.code.model.user.TypeOfInteraction;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

public class ForumThreadSpecification extends AbstractSpecification<ForumThread, ForumThreadField> {

	private static final String THREAD_CATEGORY_FIELD = "threadCategory";

	private static final String COMMENTS_FIELD = "comments";

	public ForumThreadSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		super(criteria, dependencies);
	}

	@Override
	protected Class<ForumThreadField> getFieldClass() {
		return ForumThreadField.class;
	}

	@Override
	protected Predicate buildPredicateForField(PredicateContext<ForumThread> context, ForumThreadField field) {
		return switch (field) {
			case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(context, USER_FIELD);
			case CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(context, THREAD_CATEGORY_FIELD);
			case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
				LikesAndSaves.USER_THREADS.getFieldName(), TYPE_FIELD, TypeOfInteraction.SAVE);
			case COMMENTS -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
				COMMENTS_FIELD, null, null);
		};
	}

}
