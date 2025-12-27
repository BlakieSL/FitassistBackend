package source.code.validation.media;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import source.code.config.ContextProvider;
import source.code.dto.request.media.MediaCreateDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.repository.MediaRepository;

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
		} finally {
			entityManager.setFlushMode(FlushModeType.AUTO);
		}
	}

}
