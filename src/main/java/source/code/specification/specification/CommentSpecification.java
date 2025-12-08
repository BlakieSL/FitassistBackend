package source.code.specification.specification;

import jakarta.persistence.criteria.Predicate;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.field.CommentField;
import source.code.model.thread.Comment;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

import static source.code.specification.SpecificationConstants.USER_FIELD;

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
