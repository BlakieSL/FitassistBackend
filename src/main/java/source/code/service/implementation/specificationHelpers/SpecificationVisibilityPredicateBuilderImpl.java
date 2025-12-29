package source.code.service.implementation.specificationHelpers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.utils.AuthorizationUtil;
import source.code.service.declaration.specificationHelpers.SpecificationVisibilityPredicateBuilder;

@Component
public class SpecificationVisibilityPredicateBuilderImpl implements SpecificationVisibilityPredicateBuilder {

	@Override
	public <T> Predicate buildVisibilityPredicate(CriteriaBuilder builder, Root<T> root, FilterCriteria criteria,
			String userField, String idField, String publicField) {

		if (criteria.getIsPublic() != null && criteria.getIsPublic() == Boolean.FALSE) {
			return builder.or(builder.isTrue(root.get(publicField)),
					builder.equal(root.get(userField).get(idField), AuthorizationUtil.getUserId()));
		}
		return builder.isTrue(root.get(publicField));
	}

}
