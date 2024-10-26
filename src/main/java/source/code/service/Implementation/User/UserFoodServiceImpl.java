package source.code.service.Implementation.User;

import org.springframework.stereotype.Service;
import source.code.dto.Response.FoodResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.Food.FoodMapper;
import source.code.model.Food.Food;
import source.code.model.User.User;
import source.code.model.User.UserFood;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.Declaration.User.SavedService;

import java.util.List;

@Service("userFoodService")
public class UserFoodServiceImpl
        extends GenericSavedService<Food, UserFood, FoodResponseDto>
        implements SavedService{

  public UserFoodServiceImpl(UserFoodRepository userFoodRepository,
                             FoodRepository foodRepository,
                             UserRepository userRepository,
                             FoodMapper foodMapper) {
    super(userRepository,
            foodRepository,
            userFoodRepository,
            foodMapper::toResponseDto,
            Food.class);
  }

  @Override
  protected boolean isAlreadySaved(int userId, int foodId, short type) {
    return ((UserFoodRepository) userEntityRepository)
            .existsByUserIdAndFoodIdAndType(userId, foodId, type);
  }

  @Override
  protected UserFood createUserEntity(User user, Food entity, short type) {
    return UserFood.createWithUserFoodType(user, entity, type);
  }

  @Override
  protected UserFood findUserEntity(int userId, int foodId, short type) {
    return ((UserFoodRepository) userEntityRepository)
            .findByUserIdAndFoodIdAndType(userId, foodId, type)
            .orElseThrow(() -> new RecordNotFoundException(UserFood.class, userId, foodId, type));
  }

  @Override
  protected List<UserFood> findAllByUserAndType(int userId, short type) {
    return ((UserFoodRepository) userEntityRepository).findByUserIdAndType(userId, type);
  }

  @Override
  protected Food extractEntity(UserFood userFood) {
    return userFood.getFood();
  }

  @Override
  protected long countSaves(int foodId) {
    return ((UserFoodRepository) userEntityRepository).countByFoodIdAndType(foodId, (short) 1);
  }

  @Override
  protected long countLikes(int foodId) {
    return ((UserFoodRepository) userEntityRepository).countByFoodIdAndType(foodId, (short) 2);
  }
}
