package com.fitassist.backend.service.implementation.helpers;

import com.fitassist.backend.service.declaration.helpers.ValidationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public final class ValidationServiceImpl implements ValidationService {

	private final Validator validator;

	public ValidationServiceImpl(Validator validator) {
		this.validator = validator;
	}

	@Override
	public <T> void validate(T dto, Class<?>... groups) {
		Set<ConstraintViolation<T>> violations = validator.validate(dto, groups);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

}
