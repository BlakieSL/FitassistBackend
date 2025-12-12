package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserFood;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

@Service("userFoodService")
public class UserFoodServiceImpl
        extends GenericSavedServiceWithoutType<Food, UserFood, FoodSummaryDto>
        implements SavedServiceWithoutType {

    private final FoodMapper foodMapper;
    private final ImageUrlPopulationService imagePopulationService;

    public UserFoodServiceImpl(UserRepository userRepository,
                               JpaRepository<Food, Integer> entityRepository,
                               JpaRepository<UserFood, Integer> userEntityRepository,
                               FoodMapper mapper,
                               ImageUrlPopulationService imagePopulationService) {
        super(userRepository,
                entityRepository,
                userEntityRepository,
                mapper::toSummaryDto,
                Food.class);
        this.foodMapper = mapper;
        this.imagePopulationService = imagePopulationService;
    }

    @Override
    @CacheEvict(value = CacheNames.FOODS, key = "#entityId")
    public void saveToUser(int entityId) {
        super.saveToUser(entityId);
    }

    @Override
    @CacheEvict(value = CacheNames.FOODS, key = "#entityId")
    public void deleteFromUser(int entityId) {
        super.deleteFromUser(entityId);
    }

    @Override
    public Page<BaseUserEntity> getAllFromUser(int userId, Pageable pageable) {
        return ((UserFoodRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, pageable)
                .map(uf -> {
                    FoodSummaryDto dto = foodMapper.toSummaryDto(uf.getFood());
                    dto.setUserFoodInteractionCreatedAt(uf.getCreatedAt());
                    imagePopulationService.populateFirstImageFromMediaList(
                            dto,
                            uf.getFood().getMediaList(),
                            Media::getImageName,
                            FoodSummaryDto::setImageName,
                            FoodSummaryDto::setFirstImageUrl
                    );
                    return dto;
                });
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
}
