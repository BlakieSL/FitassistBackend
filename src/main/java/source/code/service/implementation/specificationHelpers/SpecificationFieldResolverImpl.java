package source.code.service.implementation.specificationHelpers;

import org.springframework.stereotype.Component;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.service.declaration.specificationHelpers.SpecificationFieldResolver;

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
