package source.code.service.implementation.specificationHelpers;

import lombok.Getter;
import org.springframework.stereotype.Component;
import source.code.service.declaration.specificationHelpers.SpecificationFetchInitializer;
import source.code.service.declaration.specificationHelpers.SpecificationFieldResolver;
import source.code.service.declaration.specificationHelpers.SpecificationVisibilityPredicateBuilder;

@Component
@Getter
public class SpecificationDependencies {
    private final SpecificationFieldResolver fieldResolver;
    private final SpecificationFetchInitializer fetchInitializer;
    private final SpecificationVisibilityPredicateBuilder visibilityPredicateBuilder;

    public SpecificationDependencies(SpecificationFieldResolver fieldResolver,
                                     SpecificationFetchInitializer fetchInitializer,
                                     SpecificationVisibilityPredicateBuilder visibilityPredicateBuilder) {
        this.fieldResolver = fieldResolver;
        this.fetchInitializer = fetchInitializer;
        this.visibilityPredicateBuilder = visibilityPredicateBuilder;
    }
}
