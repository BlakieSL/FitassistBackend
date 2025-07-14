package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.food.FoodResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.user.User;
import source.code.model.user.UserFood;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userFoodService")
public class UserFoodServiceImpl
        extends GenericSavedServiceWithoutType<Food, UserFood, FoodResponseDto>
        implements SavedServiceWithoutType {


    public UserFoodServiceImpl(UserRepository userRepository,
                               JpaRepository<Food, Integer> entityRepository,
                               JpaRepository<UserFood, Integer> userEntityRepository,
                               FoodMapper mapper) {
        super(userRepository,
                entityRepository,
                userEntityRepository,
                mapper::toResponseDto,
                Food.class);
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId) {
        return ((UserFoodRepository) userEntityRepository)
                .existsByUserIdAndFoodId(userId, entityId);
    }

    @Override
    protected UserFood createUserEntity(User user, Food entity) {
        return UserFood.of(user, entity);
    }

    @Override
    protected UserFood findUserEntity(int userId, int entityId) {
        return ((UserFoodRepository) userEntityRepository)
                .findByUserIdAndFoodId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserFood.class,
                        userId,
                        entityId
                ));
    }

    @Override
    protected List<UserFood> findAllByUser(int userId) {
        return ((UserFoodRepository) userEntityRepository).findByUserId(userId);
    }

    @Override
    protected Food extractEntity(UserFood userEntity) {
        return userEntity.getFood();
    }

    @Override
    protected long countSaves(int entityId) {
        return ((UserFoodRepository) userEntityRepository)
                .countByFoodId(entityId);
    }

    @Override
    protected long countLikes(int entityId) {
        return 0;
    }
}
