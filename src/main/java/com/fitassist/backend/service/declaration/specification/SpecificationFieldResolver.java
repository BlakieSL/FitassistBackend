package com.fitassist.backend.service.declaration.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;

public interface SpecificationFieldResolver {

	<F extends Enum<F>> F resolveField(FilterCriteria criteria, Class<F> enumClass);

}
