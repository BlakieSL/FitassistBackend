package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserFood;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

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
    public List<BaseUserEntity> getAllFromUser(int userId, Sort.Direction sortDirection) {
        Sort sort = Sort.by(sortDirection, "createdAt");

        List<UserFood> userFoods = ((UserFoodRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, sort);

        return userFoods.stream()
                .map(uf -> {
                    FoodSummaryDto dto = foodMapper.toSummaryDto(uf.getFood());
                    dto.setUserFoodInteractionCreatedAt(uf.getCreatedAt());

                    imagePopulationService.populateFirstImageFromMediaList(
                            List.of(dto),
                            d -> uf.getFood().getMediaList(),
                            Media::getImageName,
                            FoodSummaryDto::setImageName,
                            FoodSummaryDto::setFirstImageUrl
                    );

                    return (BaseUserEntity) dto;
                })
                .toList();
    }

    @Override
    protected List<UserFood> findAllByUser(int userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return ((UserFoodRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, sort);
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
