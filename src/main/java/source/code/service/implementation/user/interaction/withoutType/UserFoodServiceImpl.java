package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.user.User;
import source.code.model.user.UserFood;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.food.FoodPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userFoodService")
public class UserFoodServiceImpl
        extends GenericSavedServiceWithoutType<Food, UserFood, FoodSummaryDto>
        implements SavedServiceWithoutType {

    private final FoodMapper foodMapper;
    private final FoodPopulationService foodPopulationService;

    public UserFoodServiceImpl(UserRepository userRepository,
                               JpaRepository<Food, Integer> entityRepository,
                               JpaRepository<UserFood, Integer> userEntityRepository,
                               FoodMapper mapper,
                               FoodPopulationService foodPopulationService) {
        super(userRepository,
                entityRepository,
                userEntityRepository,
                mapper::toSummaryDto,
                Food.class);
        this.foodMapper = mapper;
        this.foodPopulationService = foodPopulationService;
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
        Page<UserFood> userFoodPage = ((UserFoodRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, pageable);

        List<FoodSummaryDto> summaries = userFoodPage.getContent().stream()
                .map(uf -> {
                    FoodSummaryDto dto = foodMapper.toSummaryDto(uf.getFood());
                    dto.setInteractionCreatedAt(uf.getCreatedAt());
                    return dto;
                })
                .toList();

        foodPopulationService.populate(summaries);

        return new PageImpl<>(
                summaries.stream().map(dto -> (BaseUserEntity) dto).toList(),
                pageable,
                userFoodPage.getTotalElements()
        );
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
