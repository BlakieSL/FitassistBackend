package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.field.CommentField;
import source.code.model.thread.Comment;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

@AllArgsConstructor(staticName = "of")
public class CommentSpecification implements Specification<Comment> {
    private static final String USER_FIELD = "user";

    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    @Override
    public Predicate toPredicate(@NonNull Root<Comment> root,
                                 CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        CommentField field = dependencies.getFieldResolver().resolveField(criteria, CommentField.class);

        return buildPredicateForField(builder, root, field);
    }

    private Predicate buildPredicateForField(CriteriaBuilder builder, Root<Comment> root, CommentField field) {
        return switch (field) {
            case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder, criteria, root, USER_FIELD);
        };
    }
}
