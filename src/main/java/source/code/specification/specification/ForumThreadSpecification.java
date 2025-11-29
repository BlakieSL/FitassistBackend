package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.field.ForumThreadField;
import source.code.model.thread.ForumThread;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

@AllArgsConstructor(staticName = "of")
public class ForumThreadSpecification implements Specification<ForumThread> {
    private static final String USER_FIELD = "user";

    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    @Override
    public Predicate toPredicate(@NonNull Root<ForumThread> root,
                                 CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        ForumThreadField field = dependencies.getFieldResolver().resolveField(criteria, ForumThreadField.class);

        return buildPredicateForField(builder, root, field);
    }

    private Predicate buildPredicateForField(CriteriaBuilder builder, Root<ForumThread> root, ForumThreadField field) {
        return switch (field) {
            case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder, criteria, root, USER_FIELD);
        };
    }
}
