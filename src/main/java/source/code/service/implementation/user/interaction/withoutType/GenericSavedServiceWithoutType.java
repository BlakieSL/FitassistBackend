package source.code.service.implementation.user.interaction.withoutType;

import jakarta.transaction.Transactional;

import java.util.function.Function;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.utils.AuthorizationUtil;
import source.code.model.user.User;
import source.code.repository.UserRepository;
import source.code.service.declaration.user.SavedServiceWithoutType;

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
