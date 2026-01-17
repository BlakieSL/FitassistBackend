package com.fitassist.backend.service.implementation.specification;

import com.fitassist.backend.service.declaration.specification.SpecificationFieldResolver;
import com.fitassist.backend.service.declaration.specification.SpecificationVisibilityPredicateBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;

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
