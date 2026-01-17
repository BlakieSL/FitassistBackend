package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintBase;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.CommentRepository;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Mapping(target = "mediaList", ignore = true)
	public abstract CommentComplaint toCommentComplaint(ComplaintCreateDto createDto, @Context int userId);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "user", expression = "java(toUserFromUserId(userId))")
	@Mapping(target = "thread", source = "parentId", qualifiedByName = "toThreadFromParentId")
	@Mapping(target = "mediaList", ignore = true)
	public abstract ThreadComplaint toThreadComplaint(ComplaintCreateDto createDto, @Context int userId);

	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "discriminatorValue", expression = "java(getDiscriminatorValue(complaint))")
	@Mapping(target = "associatedId", expression = "java(getAssociatedId(complaint))")
	public abstract ComplaintResponseDto toResponseDto(ComplaintBase complaint);

	protected String getDiscriminatorValue(ComplaintBase complaint) {
		if (complaint instanceof CommentComplaint) {
			return "COMMENT_COMPLAINT";
		}
		else if (complaint instanceof ThreadComplaint) {
			return "THREAD_COMPLAINT";
		}
		return null;
	}

	protected Integer getAssociatedId(ComplaintBase complaint) {
		if (complaint instanceof CommentComplaint) {
			return ((CommentComplaint) complaint).getComment() != null
					? ((CommentComplaint) complaint).getComment().getId() : null;
		}
		else if (complaint instanceof ThreadComplaint) {
			return ((ThreadComplaint) complaint).getThread() != null ? ((ThreadComplaint) complaint).getThread().getId()
					: null;
		}
		return null;
	}

	@Named("toUserFromUserId")
	protected User toUserFromUserId(Integer userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
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
