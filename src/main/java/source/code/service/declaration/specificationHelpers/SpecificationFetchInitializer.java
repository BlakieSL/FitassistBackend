package source.code.service.declaration.specificationHelpers;

import jakarta.persistence.criteria.Root;

public interface SpecificationFetchInitializer {
    public <T> void initializeFetches(Root<T> root, String... fields);
}
