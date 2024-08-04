package com.example.simplefullstackproject.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public final class ValidationHelper {
    private final Validator validator;

    public ValidationHelper(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T dto, Class<?>... groups) {
        Set<ConstraintViolation<T>> errors = validator.validate(dto, groups);
        if (!errors.isEmpty()) {
            System.out.println("Validation errors detected:");
            errors.forEach(error -> System.out.println(error.getPropertyPath() + " " + error.getMessage() + " " + error.getInvalidValue()));
            throw new IllegalArgumentException("Validation failed");
        }
    }
}