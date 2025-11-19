package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.User;
import source.code.model.user.UserActivity;
import source.code.repository.MediaRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("userActivityService")
public class UserActivityServiceImpl
        extends GenericSavedServiceWithoutType<Activity, UserActivity, ActivityResponseDto>
        implements SavedServiceWithoutType {

    private final MediaRepository mediaRepository;
    private final AwsS3Service awsS3Service;

    public UserActivityServiceImpl(UserRepository userRepository,
                                   JpaRepository<Activity, Integer> entityRepository,
                                   JpaRepository<UserActivity, Integer> userEntityRepository,
                                   ActivityMapper mapper,
                                   MediaRepository mediaRepository,
                                   AwsS3Service awsS3Service) {
        super(userRepository,
                entityRepository,
                userEntityRepository,
                mapper::toResponseDto,
                Activity.class);
        this.mediaRepository = mediaRepository;
        this.awsS3Service = awsS3Service;
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId) {
        return ((UserActivityRepository) userEntityRepository)
                .existsByUserIdAndActivityId(userId, entityId);
    }

    @Override
    protected UserActivity createUserEntity(User user, Activity entity) {
        return UserActivity.of(user, entity);
    }

    @Override
    protected UserActivity findUserEntity(int userId, int entityId) {
        return ((UserActivityRepository) userEntityRepository)
                .findByUserIdAndActivityId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserActivity.class,
                        userId,
                        entityId
                ));
    }

    @Override
    public List<BaseUserEntity> getAllFromUser(int userId, String sortDirection) {
        List<ActivityResponseDto> dtos = new ArrayList<>(((UserActivityRepository) userEntityRepository)
                .findActivityDtosByUserId(userId));

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

    private void sortByInteractionDate(List<ActivityResponseDto> list, String sortDirection) {
        Comparator<ActivityResponseDto> comparator;
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            comparator = Comparator.comparing(
                    ActivityResponseDto::getUserActivityInteractionCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        } else {
            comparator = Comparator.comparing(
                    ActivityResponseDto::getUserActivityInteractionCreatedAt,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
        }
        list.sort(comparator);
    }

    @Override
    protected List<UserActivity> findAllByUser(int userId) {
        return ((UserActivityRepository) userEntityRepository).findAllByUserId(userId);
    }

    @Override
    protected Activity extractEntity(UserActivity userEntity) {
        return userEntity.getActivity();
    }

    @Override
    protected long countSaves(int entityId) {
        return ((UserActivityRepository) userEntityRepository)
                .countByActivityId(entityId);
    }

    @Override
    protected long countLikes(int entityId) {
        return 0;
    }
}
