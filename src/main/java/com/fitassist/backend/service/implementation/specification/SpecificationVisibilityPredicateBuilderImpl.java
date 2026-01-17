package com.fitassist.backend.service.implementation.specification;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.service.declaration.specification.SpecificationVisibilityPredicateBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class SpecificationVisibilityPredicateBuilderImpl implements SpecificationVisibilityPredicateBuilder {

	@Override
	public <T> Predicate buildVisibilityPredicate(CriteriaBuilder builder, Root<T> root, FilterCriteria criteria,
			String userField, String idField, String publicField) {

		if (criteria.getIsPublic() != null && criteria.getIsPublic() == Boolean.FALSE) {
			return builder.or(builder.isTrue(root.get(publicField)),
					builder.equal(root.get(userField).get(idField), AuthorizationUtil.getUserId()));
		}

		if (AuthorizationUtil.isAdmin()) {
			return builder.conjunction();
		}

		return builder.isTrue(root.get(publicField));
	}

}
