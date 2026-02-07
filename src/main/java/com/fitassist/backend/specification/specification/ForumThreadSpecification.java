package com.fitassist.backend.specification.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.PredicateContext;
import com.fitassist.backend.specification.specification.field.ForumThreadField;
import com.fitassist.backend.specification.specification.field.LikesAndSaves;
import jakarta.persistence.criteria.Predicate;

import static com.fitassist.backend.specification.SpecificationConstants.TYPE_FIELD;
import static com.fitassist.backend.specification.SpecificationConstants.USER_FIELD;

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
