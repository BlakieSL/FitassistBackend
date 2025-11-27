package source.code.service.implementation.specificationHelpers;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import source.code.service.declaration.specificationHelpers.SpecificationFetchInitializer;

@Component
public class SpecificationFetchInitializerImpl implements SpecificationFetchInitializer {
    @Override
    public <T> void initializeFetches(Root<T> root, CriteriaQuery<?> query, String... fields) {
        if (query.getResultType() == Long.class || query.getResultType() == long.class) {
            return;
        }

        for (String field : fields) {
            root.fetch(field, JoinType.LEFT);
        }
    }
}
