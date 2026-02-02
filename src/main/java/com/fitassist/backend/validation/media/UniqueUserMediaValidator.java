package com.fitassist.backend.validation.media;

import com.fitassist.backend.validation.ContextProvider;
import com.fitassist.backend.dto.request.media.MediaCreateDto;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.repository.MediaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUserMediaValidator implements ConstraintValidator<UniqueUserMedia, Object> {

	private MediaRepository mediaRepository;

	private EntityManager entityManager;

	@Override
	public void initialize(UniqueUserMedia constraintAnnotation) {
		entityManager = ContextProvider.getBean(EntityManager.class);
		this.mediaRepository = ContextProvider.getBean(MediaRepository.class);
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			entityManager.setFlushMode(FlushModeType.COMMIT);

			if (value instanceof MediaCreateDto dto) {
				if (dto.getParentType() != MediaConnectedEntity.USER) {
					return true;
				}

				return mediaRepository
					.findFirstByParentIdAndParentTypeOrderByIdAsc(dto.getParentId(), MediaConnectedEntity.USER)
					.isEmpty();
			}

			return true;
		}
		finally {
			entityManager.setFlushMode(FlushModeType.AUTO);
		}
	}

}
