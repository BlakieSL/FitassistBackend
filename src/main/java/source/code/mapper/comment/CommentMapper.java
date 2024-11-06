package source.code.mapper.comment;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.Request.comment.CommentCreateDto;
import source.code.dto.Request.comment.CommentUpdateDto;
import source.code.dto.Response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.forum.Comment;
import source.code.model.forum.ForumThread;
import source.code.model.user.User;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.UserRepository;

import java.util.List;
import java.util.Set;

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
    @Mapping(target = "repliesIds", source = "replies", qualifiedByName = "repliesToRepliesIds")
    public abstract CommentResponseDto toResponseDto(Comment comment);

    @Mapping(target = "text", source = "text")
    @Mapping(target = "thread", source = "threadId", qualifiedByName = "threadIdToThread")
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    @Mapping(target = "parentComment", source = "parentCommentId", qualifiedByName = "parentCommentIdToParentComment")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "userCommentLikes", ignore = true)
    public abstract Comment toEntity(CommentCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "text", source = "text")
    @Mapping(target = "thread", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "userCommentLikes", ignore = true)
    public abstract void updateCommentFromDto(
            @MappingTarget Comment comment, CommentUpdateDto updateDto);

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
        return parentComment.getId();
    }

    @Named("repliesToRepliesIds")
    protected List<Integer> repliesToRepliesIds(Set<Comment> replies) {
        return replies.stream()
                .map(Comment::getId)
                .toList();
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
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> RecordNotFoundException.of(Comment.class, parentCommentId));
    }
}
