package source.code.service.implementation.user.interaction.withType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.utils.AuthorizationUtil;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.repository.UserRepository;

import java.util.Optional;
import java.util.function.Function;

public abstract class GenericSavedService<T, U, R> {
    protected final UserRepository userRepository;
    protected final JpaRepository<T, Integer> entityRepository;
    protected final JpaRepository<U, Integer> userEntityRepository;
    protected final Function<T, R> map;
    protected final Class<T> entityType;
    protected final Class<U> userEntityType;

    public GenericSavedService(UserRepository userRepository,
                               JpaRepository<T, Integer> entityRepository,
                               JpaRepository<U, Integer> userEntityRepository,
                               Function<T, R> map,
                               Class<T> entityType,
                               Class<U> userEntityType) {
        this.userRepository = userRepository;
        this.entityRepository = entityRepository;
        this.userEntityRepository = userEntityRepository;
        this.map = map;
        this.entityType = entityType;
        this.userEntityType = userEntityType;
    }

    @Transactional
    public void saveToUser(int entityId, TypeOfInteraction type) {
        int userId = AuthorizationUtil.getUserId();
        if (isAlreadySaved(userId, entityId, type)) {
            throw new NotUniqueRecordException(User.class, userId, entityId, type);
        }

        TypeOfInteraction oppositeType = type.getOpposite();
        if (oppositeType != null) {
            findUserEntityOptional(userId, entityId, oppositeType)
                    .ifPresent(userEntityRepository::delete);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));

        T entity = entityRepository.findById(entityId)
                .orElseThrow(() -> RecordNotFoundException.of(entityType, entityId));

        U userEntity = createUserEntity(user, entity, type);
        userEntityRepository.save(userEntity);
    }

    @Transactional
    public void deleteFromUser(int entityId, TypeOfInteraction type) {
        int userId = AuthorizationUtil.getUserId();
        U userEntity = findUserEntity(userId, entityId, type);
        userEntityRepository.delete(userEntity);
    }

    protected abstract boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type);

    protected abstract U createUserEntity(User user, T entity, TypeOfInteraction type);

    protected U findUserEntity(int userId, int entityId, TypeOfInteraction type) {
        return findUserEntityOptional(userId, entityId, type)
                .orElseThrow(() -> RecordNotFoundException.of(
                        userEntityType,
                        userId,
                        entityId,
                        type
                ));
    }

    protected abstract Optional<U> findUserEntityOptional(int userId, int entityId, TypeOfInteraction type);
}
