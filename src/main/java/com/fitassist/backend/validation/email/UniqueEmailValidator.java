package com.fitassist.backend.validation.email;

import com.fitassist.backend.validation.ContextProvider;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailDomain, Object> {

	UserRepository userRepository;

	private EntityManager entityManager;

	@Override
	public void initialize(UniqueEmailDomain constraintAnnotation) {
		entityManager = ContextProvider.getBean(EntityManager.class);
		this.userRepository = ContextProvider.getBean(UserRepository.class);
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			entityManager.setFlushMode(FlushModeType.COMMIT);

			if (value instanceof UserUpdateDto dto) {
				if (dto.getEmail() == null) {
					return true;
				}

				Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

				if (existingUser.isEmpty()) {
					return true;
				}

				Integer existingUserId = existingUser.get().getId();
				return existingUserId.equals(dto.getId());
			}

			if (value instanceof String email) {
				return !userRepository.existsByEmail(email);
			}

			return true;
		}
		finally {
			entityManager.setFlushMode(FlushModeType.AUTO);
		}
	}

}
