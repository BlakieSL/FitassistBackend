package source.code.service.implementation.specificationHelpers;

import lombok.Getter;
import org.springframework.stereotype.Component;
import source.code.service.declaration.specificationHelpers.SpecificationFieldResolver;
import source.code.service.declaration.specificationHelpers.SpecificationVisibilityPredicateBuilder;

@Component
@Getter
public class SpecificationDependencies {

	private final SpecificationFieldResolver fieldResolver;

	private final SpecificationVisibilityPredicateBuilder visibilityPredicateBuilder;

	public SpecificationDependencies(SpecificationFieldResolver fieldResolver,
									 SpecificationVisibilityPredicateBuilder visibilityPredicateBuilder) {
		this.fieldResolver = fieldResolver;
		this.visibilityPredicateBuilder = visibilityPredicateBuilder;
	}

}
