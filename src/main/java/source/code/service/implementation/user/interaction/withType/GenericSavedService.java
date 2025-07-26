package source.code.service.implementation.user.interaction.withType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.repository.UserRepository;

import java.util.List;
import java.util.function.Function;

public abstract class GenericSavedService<T, U, R> {
    protected final UserRepository userRepository;
    protected final JpaRepository<T, Integer> entityRepository;
    protected final JpaRepository<U, Integer> userEntityRepository;
    protected final Function<T, R> map;
    protected final Class<T> entityType;

    public GenericSavedService(UserRepository userRepository,
                               JpaRepository<T, Integer> entityRepository,
                               JpaRepository<U, Integer> userEntityRepository,
                               Function<T, R> map,
                               Class<T> entityType) {
        this.userRepository = userRepository;
        this.entityRepository = entityRepository;
        this.userEntityRepository = userEntityRepository;
        this.map = map;
        this.entityType = entityType;
    }

    @Transactional
    public void saveToUser(int entityId, TypeOfInteraction type) {
        int userId = AuthorizationUtil.getUserId();
        if (isAlreadySaved(userId, entityId, type)) {
            throw NotUniqueRecordException.of(
                    "User with id: " + userId
                            + " already has entity with id: " + entityId
                            + " and type: " + type
            );
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

    public List<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type) {
        return findAllByUserAndType(userId, type).stream()
                .map(this::extractEntity)
                .map(entity -> (BaseUserEntity) map.apply(entity))
                .toList();
    }

    public LikesAndSavesResponseDto calculateLikesAndSaves(int entityId) {
        entityRepository.findById(entityId)
                .orElseThrow(() -> RecordNotFoundException.of(entityType, entityId));

        return LikesAndSavesResponseDto.of(countLikes(entityId), countSaves(entityId));
    }

    protected abstract boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type);

    protected abstract U createUserEntity(User user, T entity, TypeOfInteraction type);

    protected abstract U findUserEntity(int userId, int entityId, TypeOfInteraction type);

    protected abstract List<U> findAllByUserAndType(int userId, TypeOfInteraction type);

    protected abstract T extractEntity(U userEntity);

    protected abstract long countSaves(int entityId);

    protected abstract long countLikes(int entityId);
}
