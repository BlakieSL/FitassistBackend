package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserActivity;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userActivityService")
public class UserActivityServiceImpl
        extends GenericSavedServiceWithoutType<Activity, UserActivity, ActivitySummaryDto>
        implements SavedServiceWithoutType {

    private final ActivityMapper activityMapper;
    private final ImageUrlPopulationService imagePopulationService;

    public UserActivityServiceImpl(UserRepository userRepository,
                                   JpaRepository<Activity, Integer> entityRepository,
                                   JpaRepository<UserActivity, Integer> userEntityRepository,
                                   ActivityMapper mapper,
                                   ImageUrlPopulationService imagePopulationService) {
        super(userRepository,
                entityRepository,
                userEntityRepository,
                mapper::toSummaryDto,
                Activity.class);
        this.activityMapper = mapper;
        this.imagePopulationService = imagePopulationService;
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
    public Page<BaseUserEntity> getAllFromUser(int userId, Pageable pageable) {
        return ((UserActivityRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, pageable)
                .map(ua -> {
                    ActivitySummaryDto dto = activityMapper.toSummaryDto(ua.getActivity());
                    dto.setUserActivityInteractionCreatedAt(ua.getCreatedAt());
                    imagePopulationService.populateFirstImageFromMediaList(dto, ua.getActivity().getMediaList(),
                            Media::getImageName, ActivitySummaryDto::setImageName, ActivitySummaryDto::setFirstImageUrl);
                    return dto;
                });
    }

    @Override
    protected List<UserActivity> findAllByUser(int userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return ((UserActivityRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, sort);
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
