package source.code.mapper.complaint;


import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.thread.Comment;
import source.code.model.complaint.CommentComplaint;
import source.code.model.thread.ForumThread;
import source.code.model.complaint.ThreadComplaint;
import source.code.model.user.User;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class ComplaintMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ForumThreadRepository threadRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", expression = "java(toUserFromUserId(userId))")
    @Mapping(target = "comment", source = "parentId", qualifiedByName = "toCommentFromParentId")
    public abstract CommentComplaint toCommentComplaint(
            ComplaintCreateDto createDto, @Context int userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", expression = "java(toUserFromUserId(userId))")
    @Mapping(target = "thread", source = "parentId", qualifiedByName = "toThreadFromParentId")
    public abstract ThreadComplaint toThreadComplaint(
            ComplaintCreateDto createDto, @Context int userId);

    @Named("toUserFromUserId")
    protected User toUserFromUserId(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
    }

    @Named("toCommentFromParentId")
    protected Comment toCommentFromParentId(Integer parentId) {
        return commentRepository.findById(parentId)
                .orElseThrow(() -> RecordNotFoundException.of(Comment.class, parentId));
    }

    @Named("toThreadFromParentId")
    protected ForumThread toThreadFromParentId(Integer parentId) {
        return threadRepository.findById(parentId)
                .orElseThrow(() -> RecordNotFoundException.of(ForumThread.class, parentId));
    }
}
