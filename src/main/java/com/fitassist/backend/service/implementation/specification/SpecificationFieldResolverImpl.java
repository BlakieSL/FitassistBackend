package com.fitassist.backend.service.implementation.specification;

import org.springframework.stereotype.Component;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.exception.InvalidFilterKeyException;
import com.fitassist.backend.service.declaration.specification.SpecificationFieldResolver;

@Component
public class SpecificationFieldResolverImpl implements SpecificationFieldResolver {

	@Override
	public <F extends Enum<F>> F resolveField(FilterCriteria criteria, Class<F> enumClass) {
		try {
			return Enum.valueOf(enumClass, criteria.getFilterKey());
		}
		catch (IllegalArgumentException e) {
			throw new InvalidFilterKeyException(criteria.getFilterKey());
		}
	}

}
