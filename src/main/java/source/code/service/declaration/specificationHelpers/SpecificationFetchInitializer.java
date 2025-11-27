package source.code.service.declaration.specificationHelpers;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public interface SpecificationFetchInitializer {
    <T> void initializeFetches(Root<T> root, CriteriaQuery<?> query, String... fields);
}
