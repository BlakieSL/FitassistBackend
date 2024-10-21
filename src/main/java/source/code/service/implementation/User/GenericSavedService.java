package source.code.service.implementation.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.model.Recipe.Recipe;
import source.code.model.User.User;
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
  public void saveToUser(int userId, int entityId, short type) {
    if (isAlreadySaved(userId, entityId, type)) {
      throw new NotUniqueRecordException(
              "User with id: " + userId
                      + " already has entity with id: " + entityId
                      + " and type: " + type);
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RecordNotFoundException(User.class, userId));

    T entity = entityRepository.findById(entityId)
            .orElseThrow(() -> new RecordNotFoundException(entityType, entityId));

    U userEntity = createUserEntity(user, entity, type);
    userEntityRepository.save(userEntity);
  }

  @Transactional
  public void deleteFromUser(int userId, int entityId, short type) {
    U userEntity = findUserEntity(userId, entityId, type);
    userEntityRepository.delete(userEntity);
  }

  public List<R> getAllFromUser(int userId, short type) {
    return findAllByUserAndType(userId, type).stream()
            .map(this::extractEntity)
            .map(map)
            .toList();
  }

  public LikesAndSavesResponseDto calculateLikesAndSaves(int entityId) {
    entityRepository.findById(entityId)
            .orElseThrow(() -> new RecordNotFoundException(entityType, entityId));

    long saves = countSaves(entityId);
    long likes = countLikes(entityId);

    return new LikesAndSavesResponseDto(likes, saves);
  }

  protected abstract boolean isAlreadySaved(int userId, int entityId, short type);

  protected abstract U createUserEntity(User user, T entity, short type);

  protected abstract U findUserEntity(int userId, int entityId, short type);

  protected abstract List<U> findAllByUserAndType(int userId, short type);

  protected abstract T extractEntity(U userEntity);

  protected abstract long countSaves(int entityId);

  protected abstract long countLikes(int entityId);
}
