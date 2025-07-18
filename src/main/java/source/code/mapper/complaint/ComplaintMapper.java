package source.code.mapper.complaint;


import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.dto.response.comment.ComplaintResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ComplaintBase;
import source.code.model.complaint.ThreadComplaint;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
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

    @Mapping(target = "status", source = "status")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "discriminatorValue", expression = "java(getDiscriminatorValue(complaint))")
    @Mapping(target = "associatedId", expression = "java(getAssociatedId(complaint))")
    public abstract ComplaintResponseDto toResponseDto(ComplaintBase complaint);

    protected String getDiscriminatorValue(ComplaintBase complaint) {
        if (complaint instanceof CommentComplaint) {
            return "COMMENT_COMPLAINT";
        } else if (complaint instanceof ThreadComplaint) {
            return "THREAD_COMPLAINT";
        }
        return null;
    }

    protected Integer getAssociatedId(ComplaintBase complaint) {
        if (complaint instanceof CommentComplaint) {
            return ((CommentComplaint) complaint).getComment() != null ?
                    ((CommentComplaint) complaint).getComment().getId() : null;
        } else if (complaint instanceof ThreadComplaint) {
            return ((ThreadComplaint) complaint).getThread() != null ?
                    ((ThreadComplaint) complaint).getThread().getId() : null;
        }
        return null;
    }

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
