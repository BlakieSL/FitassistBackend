package source.code.mapper.forumThread;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.forum.Comment;
import source.code.model.forum.ForumThread;
import source.code.model.forum.ThreadCategory;
import source.code.model.user.User;
import source.code.repository.CommentRepository;
import source.code.repository.ThreadCategoryRepository;
import source.code.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class ForumThreadMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ThreadCategoryRepository threadCategoryRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "dateCreated", source = "dateCreated")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "views", source = "views")
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    @Mapping(target = "threadCategoryId", source = "threadCategory", qualifiedByName = "threadCategoryToThreadCategoryId")
    @Mapping(target = "commentIds", source = "comments", qualifiedByName = "commentsToCommentIds")
    public abstract ForumThreadResponseDto toResponseDto(ForumThread forumThread);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    @Mapping(target = "threadCategory", source = "threadCategoryId", qualifiedByName = "threadCategoryIdToThreadCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "userThreadSubscriptions", ignore = true)
    public abstract ForumThread toEntity(ForumThreadCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "threadCategory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "userThreadSubscriptions", ignore = true)
    public abstract void update(@MappingTarget ForumThread forumThread, ForumThreadUpdateDto updateDto);

    @Named("userToUserId")
    protected Integer userToUserId(User user) {
        return user.getId();
    }

    @Named("threadCategoryToThreadCategoryId")
    protected Integer threadCategoryToThreadCategoryId(ThreadCategory threadCategory) {
        return threadCategory.getId();
    }

    @Named("commentsToCommentIds")
    protected Integer commentsToCommentIds(Comment comment) {
        return comment.getId();
    }

    @Named("userIdToUser")
    protected User userIdToUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
    }

    @Named("threadCategoryIdToThreadCategory")
    protected ThreadCategory threadCategoryIdToThreadCategory(Integer threadCategoryId) {
        return threadCategoryRepository.findById(threadCategoryId)
                .orElseThrow(() -> RecordNotFoundException.of(ThreadCategory.class, threadCategoryId));
    }

    @Named("commentIdToComment")
    protected Comment commentIdToComment(Integer commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> RecordNotFoundException.of(Comment.class, commentId));
    }
}
