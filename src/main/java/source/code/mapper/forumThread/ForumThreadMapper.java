package source.code.mapper.forumThread;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.model.thread.ThreadCategory;
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
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "views", source = "views")
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    @Mapping(target = "threadCategoryId", source = "threadCategory", qualifiedByName = "threadCategoryToThreadCategoryId")
    public abstract ForumThreadResponseDto toResponseDto(ForumThread forumThread);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "viewsCount", source = "views")
    @Mapping(target = "savesCount", expression = "java(forumThread.getUserThreads() != null ? forumThread.getUserThreads().size() : 0)")
    @Mapping(target = "commentsCount", expression = "java(forumThread.getComments() != null ? forumThread.getComments().size() : 0)")
    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorImageUrl", ignore = true)
    public abstract ForumThreadSummaryDto toSummaryDto(ForumThread forumThread);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "user", expression = "java(userIdToUser(userId))")
    @Mapping(target = "threadCategory", source = "threadCategoryId", qualifiedByName = "threadCategoryIdToThreadCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "userThreads", ignore = true)
    public abstract ForumThread toEntity(ForumThreadCreateDto createDto, @Context int userId);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "threadCategory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "userThreads", ignore = true)
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
