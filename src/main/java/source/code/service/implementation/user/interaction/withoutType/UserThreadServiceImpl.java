package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;
import source.code.model.user.UserThread;
import source.code.repository.ForumThreadRepository;
import source.code.repository.UserRepository;
import source.code.repository.UserThreadRepository;
import source.code.service.declaration.thread.ForumThreadPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userThreadService")
public class UserThreadServiceImpl
        extends GenericSavedServiceWithoutType<ForumThread, UserThread, ForumThreadSummaryDto>
        implements SavedServiceWithoutType {

    private final ForumThreadMapper forumThreadMapper;
    private final ForumThreadPopulationService forumThreadPopulationService;

    public UserThreadServiceImpl(UserThreadRepository userThreadRepository,
                                 ForumThreadRepository forumThreadRepository,
                                 UserRepository userRepository,
                                 ForumThreadMapper forumThreadMapper,
                                 ForumThreadPopulationService forumThreadPopulationService) {
        super(userRepository,
                forumThreadRepository,
                userThreadRepository,
                forumThreadMapper::toSummaryDto,
                ForumThread.class);
        this.forumThreadMapper = forumThreadMapper;
        this.forumThreadPopulationService = forumThreadPopulationService;
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
    public Page<BaseUserEntity> getAllFromUser(int userId, Pageable pageable) {
        Page<UserThread> userThreadPage = ((UserThreadRepository) userEntityRepository)
                .findByUserIdWithThread(userId, pageable);

        if (userThreadPage.isEmpty()) return new PageImpl<>(List.of(), pageable, 0);

        List<ForumThreadSummaryDto> summaries = userThreadPage.getContent().stream()
                .map(ut -> {
                    ForumThreadSummaryDto dto = forumThreadMapper.toSummaryDto(ut.getForumThread());
                    dto.setUserThreadInteractionCreatedAt(ut.getCreatedAt());
                    return dto;
                })
                .toList();

        forumThreadPopulationService.populate(summaries);

        return new PageImpl<>(summaries.stream().map(dto -> (BaseUserEntity) dto).toList(),
                pageable, userThreadPage.getTotalElements());
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
