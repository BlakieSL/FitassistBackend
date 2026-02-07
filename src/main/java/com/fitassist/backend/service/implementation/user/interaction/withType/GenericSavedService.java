package com.fitassist.backend.service.implementation.user.interaction.withType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import com.fitassist.backend.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public abstract class GenericSavedService<T, U, R> {

	protected final UserRepository userRepository;

	protected final JpaRepository<T, Integer> entityRepository;

	protected final JpaRepository<U, Integer> userEntityRepository;

	protected final Class<T> entityType;

	protected final Class<U> userEntityType;

	public GenericSavedService(UserRepository userRepository, JpaRepository<T, Integer> entityRepository,
			JpaRepository<U, Integer> userEntityRepository, Class<T> entityType, Class<U> userEntityType) {
		this.userRepository = userRepository;
		this.entityRepository = entityRepository;
		this.userEntityRepository = userEntityRepository;
		this.entityType = entityType;
		this.userEntityType = userEntityType;
	}

	@Transactional
	public InteractionResponseDto saveToUser(int entityId, TypeOfInteraction type) {
		int userId = AuthorizationUtil.getUserId();
		if (isAlreadySaved(userId, entityId, type)) {
			throw new NotUniqueRecordException(User.class, userId, entityId, type);
		}

		TypeOfInteraction oppositeType = type.getOpposite();
		if (oppositeType != null) {
			findUserEntityOptional(userId, entityId, oppositeType).ifPresent(userEntityRepository::delete);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));

		T entity = entityRepository.findById(entityId)
			.orElseThrow(() -> RecordNotFoundException.of(entityType, entityId));

		U userEntity = createUserEntity(user, entity, type);
		userEntityRepository.save(userEntity);

		return toResponseDto(entityId, type, true, oppositeType);
	}

	private InteractionResponseDto toResponseDto(int entityId, TypeOfInteraction type, boolean interacted,
			TypeOfInteraction oppositeType) {
		InteractionResponseDto response = new InteractionResponseDto();

		type.mapInteraction(response, interacted, countByEntityIdAndType(entityId, type));

		if (oppositeType != null) {
			oppositeType.mapInteraction(response, false, countByEntityIdAndType(entityId, oppositeType));
		}

		return response;
	}

	@Transactional
	public InteractionResponseDto deleteFromUser(int entityId, TypeOfInteraction type) {
		int userId = AuthorizationUtil.getUserId();
		U userEntity = findUserEntity(userId, entityId, type);
		userEntityRepository.delete(userEntity);

		return toResponseDto(entityId, type, false, null);
	}

	protected abstract long countByEntityIdAndType(int entityId, TypeOfInteraction type);

	protected abstract boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type);

	protected abstract U createUserEntity(User user, T entity, TypeOfInteraction type);

	protected U findUserEntity(int userId, int entityId, TypeOfInteraction type) {
		return findUserEntityOptional(userId, entityId, type)
			.orElseThrow(() -> RecordNotFoundException.of(userEntityType, userId, entityId, type));
	}

	protected abstract Optional<U> findUserEntityOptional(int userId, int entityId, TypeOfInteraction type);

}
