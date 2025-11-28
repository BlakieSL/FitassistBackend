package source.code.mapper.comment;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.comment.CommentUpdateDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.UserRepository;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ForumThreadRepository forumThreadRepository;

    @Mapping(target = "threadId", source = "thread", qualifiedByName = "threadToThreadId")
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    @Mapping(target = "parentCommentId", source = "parentComment", qualifiedByName = "parentCommentToParentCommentId")
    @Mapping(target = "replies", ignore = true)
    public abstract CommentResponseDto toResponseDto(Comment comment);

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "dislikesCount", ignore = true)
    @Mapping(target = "repliesCount", ignore = true)
    @Mapping(target = "authorImageName", ignore = true)
    @Mapping(target = "authorImageUrl", ignore = true)
    @Mapping(target = "userCommentInteractionCreatedAt", ignore = true)
    public abstract CommentSummaryDto toSummaryDto(Comment comment);

    @Mapping(target = "text", source = "text")
    @Mapping(target = "thread", source = "threadId", qualifiedByName = "threadIdToThread")
    @Mapping(target = "user", expression = "java(userIdToUser(userId))")
    @Mapping(target = "parentComment", source = "parentCommentId", qualifiedByName = "parentCommentIdToParentComment")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "userCommentLikes", ignore = true)
    public abstract Comment toEntity(CommentCreateDto createDto, @Context int userId);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "text", source = "text")
    @Mapping(target = "thread", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "userCommentLikes", ignore = true)
    public abstract void update(@MappingTarget Comment comment, CommentUpdateDto updateDto);

    @Named("threadToThreadId")
    protected Integer threadToThreadId(ForumThread forumThread) {
        return forumThread.getId();
    }

    @Named("userToUserId")
    protected Integer userToUserId(User user) {
        return user.getId();
    }

    @Named("parentCommentToParentCommentId")
    protected Integer parentCommentToParentCommentId(Comment parentComment) {
        return Optional.ofNullable(parentComment)
                .map(Comment::getId)
                .orElse(null);
    }

    @Named("threadIdToThread")
    protected ForumThread threadIdToThread(Integer threadId) {
        return forumThreadRepository.findById(threadId)
                .orElseThrow(() -> RecordNotFoundException.of(ForumThread.class, threadId));
    }

    @Named("userIdToUser")
    protected User userIdToUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
    }

    @Named("parentCommentIdToParentComment")
    protected Comment parentCommentIdToParentComment(Integer parentCommentId) {
        return Optional.ofNullable(parentCommentId)
                .map(id -> commentRepository.findById(id)
                        .orElseThrow(() -> RecordNotFoundException.of(Comment.class, id)))
                .orElse(null);
    }
}
