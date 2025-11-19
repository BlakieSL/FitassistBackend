package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.stereotype.Service;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;
import source.code.model.user.UserThread;
import source.code.repository.ForumThreadRepository;
import source.code.repository.MediaRepository;
import source.code.repository.UserRepository;
import source.code.repository.UserThreadRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;
@Service("userThreadService")
public class UserThreadServiceImpl
        extends GenericSavedServiceWithoutType<ForumThread, UserThread, ForumThreadSummaryDto>
        implements SavedServiceWithoutType {

    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;

    public UserThreadServiceImpl(UserThreadRepository userThreadRepository,
                                 ForumThreadRepository forumThreadRepository,
                                 UserRepository userRepository,
                                 ForumThreadMapper forumThreadMapper,
                                 MediaRepository mediaRepository,
                                 AwsS3Service s3Service) {
        super(userRepository,
                forumThreadRepository,
                userThreadRepository,
                forumThreadMapper::toSummaryDto,
                ForumThread.class);
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId) {
        return ((UserThreadRepository) userEntityRepository)
                .existsByUserIdAndForumThreadId(userId, entityId);
    }

    @Override
    protected UserThread createUserEntity(User user, ForumThread entity) {
        return UserThread.of(user, entity);
    }

    @Override
    protected UserThread findUserEntity(int userId, int entityId) {
        return ((UserThreadRepository) userEntityRepository)
                .findByUserIdAndForumThreadId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserThread.class,
                        userId,
                        entityId
                ));
    }

    @Override
    public List<BaseUserEntity> getAllFromUser(int userId, String sortDirection) {
        List<ForumThreadSummaryDto> dtos = ((UserThreadRepository) userEntityRepository)
                .findThreadSummaryByUserId(userId);

        dtos.forEach(dto -> {
            if (dto.getAuthorImageName() != null) {
                dto.setAuthorImageUrl(s3Service.getImage(dto.getAuthorImageName()));
            }
        });

        return dtos.stream()
                .map(dto -> (BaseUserEntity) dto)
                .toList();
    }

    @Override
    protected List<UserThread> findAllByUser(int userId) {
        return ((UserThreadRepository) userEntityRepository).findAllByUserId(userId);
    }

    @Override
    protected ForumThread extractEntity(UserThread userEntity) {
        return userEntity.getForumThread();
    }

    @Override
    protected long countSaves(int entityId) {
        return 0;
    }

    @Override
    protected long countLikes(int entityId) {
        return 0;
    }
}
