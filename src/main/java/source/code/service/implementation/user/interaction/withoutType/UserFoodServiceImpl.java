package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.user.User;
import source.code.model.user.UserFood;
import source.code.repository.MediaRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("userFoodService")
public class UserFoodServiceImpl
        extends GenericSavedServiceWithoutType<Food, UserFood, FoodSummaryDto>
        implements SavedServiceWithoutType {

    private final MediaRepository mediaRepository;
    private final AwsS3Service awsS3Service;

    public UserFoodServiceImpl(UserRepository userRepository,
                               JpaRepository<Food, Integer> entityRepository,
                               JpaRepository<UserFood, Integer> userEntityRepository,
                               FoodMapper mapper,
                               MediaRepository mediaRepository,
                               AwsS3Service awsS3Service) {
        super(userRepository,
                entityRepository,
                userEntityRepository,
                mapper::toSummaryDto,
                Food.class);
        this.mediaRepository = mediaRepository;
        this.awsS3Service = awsS3Service;
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
    public List<BaseUserEntity> getAllFromUser(int userId, String sortDirection) {
        List<FoodSummaryDto> dtos = new ArrayList<>(((UserFoodRepository) userEntityRepository)
                .findFoodDtosByUserId(userId));

        dtos.forEach(dto -> {
            if (dto.getImageName() != null) {
                dto.setFirstImageUrl(awsS3Service.getImage(dto.getImageName()));
            }
        });

        sortByInteractionDate(dtos, sortDirection);

        return dtos.stream()
                .map(dto -> (BaseUserEntity) dto)
                .toList();
    }

    private void sortByInteractionDate(List<FoodSummaryDto> list, String sortDirection) {
        Comparator<FoodSummaryDto> comparator;
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            comparator = Comparator.comparing(
                    FoodSummaryDto::getUserFoodInteractionCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        } else {
            comparator = Comparator.comparing(
                    FoodSummaryDto::getUserFoodInteractionCreatedAt,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
        }
        list.sort(comparator);
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
