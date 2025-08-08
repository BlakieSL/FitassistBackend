package source.code.service.declaration.specificationHelpers;

import source.code.dto.pojo.FilterCriteria;

public interface SpecificationFieldResolver {
    public <F extends Enum<F>> F resolveField(FilterCriteria criteria, Class<F> enumClass);
}
