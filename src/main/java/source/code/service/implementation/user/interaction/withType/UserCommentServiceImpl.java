package source.code.service.implementation.user.interaction.withType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.comment.CommentMapper;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserComment;
import source.code.repository.UserCommentRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.SavedService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("userCommentService")
public class UserCommentServiceImpl
        extends GenericSavedService<Comment, UserComment, CommentResponseDto>
        implements SavedService {

    private final AwsS3Service awsS3Service;

    public UserCommentServiceImpl(UserRepository userRepository,
                                  JpaRepository<Comment, Integer> entityRepository,
                                  JpaRepository<UserComment, Integer> userEntityRepository,
                                  CommentMapper mapper,
                                  AwsS3Service awsS3Service) {
        super(userRepository, entityRepository, userEntityRepository, mapper::toResponseDto, Comment.class);
        this.awsS3Service = awsS3Service;
    }

    @Override
    public List<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Sort.Direction sortDirection) {
        List<CommentSummaryDto> dtos = new ArrayList<>(((UserCommentRepository) userEntityRepository)
                .findCommentSummaryByUserIdAndType(userId, type));

        dtos.forEach(dto -> {
            if (dto.getAuthorImageUrl() != null) {
                dto.setAuthorImageUrl(awsS3Service.getImage(dto.getAuthorImageUrl()));
            }
        });

        sortByInteractionDate(dtos, sortDirection);

        return dtos.stream()
                .map(dto -> (BaseUserEntity) dto)
                .toList();
    }

    private void sortByInteractionDate(List<CommentSummaryDto> list, Sort.Direction sortDirection) {
        Comparator<CommentSummaryDto> comparator = sortDirection == Sort.Direction.ASC
                ? Comparator.comparing(
                        CommentSummaryDto::getUserCommentInteractionCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                : Comparator.comparing(
                        CommentSummaryDto::getUserCommentInteractionCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        list.sort(comparator);
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type) {
        return ((UserCommentRepository) userEntityRepository)
                .existsByUserIdAndCommentIdAndType(userId, entityId, type);
    }

    @Override
    protected UserComment createUserEntity(User user, Comment entity, TypeOfInteraction type) {
        if (type == TypeOfInteraction.SAVE) {
            throw new NotSupportedInteractionTypeException("Cannot save a comment as a saved entity.");
        }
        return UserComment.of(user, entity, type);
    }

    @Override
    protected UserComment findUserEntity(int userId, int entityId, TypeOfInteraction type) {
        return ((UserCommentRepository) userEntityRepository)
                .findByUserIdAndCommentIdAndType(userId, entityId, type)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserComment.class,
                        userId,
                        entityId,
                        type
                ));
    }

    @Override
    protected List<UserComment> findAllByUserAndType(int userId, TypeOfInteraction type) {
        return ((UserCommentRepository) userEntityRepository)
                .findByUserIdAndType(userId, type);
    }

    @Override
    protected Comment extractEntity(UserComment userEntity) {
        return userEntity.getComment();
    }

    @Override
    protected long countSaves(int entityId) {
        return 0;
    }

    @Override
    protected long countLikes(int entityId) {
        long likes = ((UserCommentRepository) userEntityRepository)
                .countByCommentIdAndType(entityId, TypeOfInteraction.LIKE);
        long dislikes = ((UserCommentRepository) userEntityRepository)
                .countByCommentIdAndType(entityId, TypeOfInteraction.DISLIKE);

        return likes - dislikes;
    }
}
