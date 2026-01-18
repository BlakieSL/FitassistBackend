package com.fitassist.backend.service.implementation.user.interaction.withoutType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

public abstract class GenericSavedServiceWithoutType<T, U, R> implements SavedServiceWithoutType {

	protected final UserRepository userRepository;

	protected final JpaRepository<T, Integer> entityRepository;

	protected final JpaRepository<U, Integer> userEntityRepository;

	protected final Function<T, R> map;

	protected final Class<T> entityType;

	public GenericSavedServiceWithoutType(UserRepository userRepository, JpaRepository<T, Integer> entityRepository,
			JpaRepository<U, Integer> userEntityRepository, Function<T, R> map, Class<T> entityType) {
		this.userRepository = userRepository;
		this.entityRepository = entityRepository;
		this.userEntityRepository = userEntityRepository;
		this.map = map;
		this.entityType = entityType;
	}

	@Override
	@Transactional
	public void saveToUser(int entityId) {
		int userId = AuthorizationUtil.getUserId();
		if (isAlreadySaved(userId, entityId)) {
			throw new NotUniqueRecordException(User.class, userId, entityId);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));

		T entity = entityRepository.findById(entityId)
			.orElseThrow(() -> RecordNotFoundException.of(entityType, entityId));

		U userEntity = createUserEntity(user, entity);
		userEntityRepository.save(userEntity);
	}

	@Override
	@Transactional
	public void deleteFromUser(int entityId) {
		int userId = AuthorizationUtil.getUserId();
		U userEntity = findUserEntity(userId, entityId);
		userEntityRepository.delete(userEntity);
	}

	protected abstract boolean isAlreadySaved(int userId, int entityId);

	protected abstract U createUserEntity(User user, T entity);

	protected abstract U findUserEntity(int userId, int entityId);

}
