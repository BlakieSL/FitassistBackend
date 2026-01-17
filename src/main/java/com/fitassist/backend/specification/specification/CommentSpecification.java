package com.fitassist.backend.specification.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.PredicateContext;
import com.fitassist.backend.specification.specification.field.CommentField;
import jakarta.persistence.criteria.Predicate;

import static com.fitassist.backend.specification.SpecificationConstants.USER_FIELD;

public class CommentSpecification extends AbstractSpecification<Comment, CommentField> {

	public CommentSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		super(criteria, dependencies);
	}

	@Override
	protected Class<CommentField> getFieldClass() {
		return CommentField.class;
	}

	@Override
	protected Predicate buildPredicateForField(PredicateContext<Comment> context, CommentField field) {
		return switch (field) {
			case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(context, USER_FIELD);
		};
	}

}
