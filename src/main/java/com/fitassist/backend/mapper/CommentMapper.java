package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.request.comment.CommentCreateDto;
import com.fitassist.backend.dto.request.comment.CommentUpdateDto;
import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.helper.CommonMappingHelper;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.CommentRepository;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class CommentMapper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private ForumThreadRepository forumThreadRepository;

	@Mapping(target = "threadId", source = "thread.id")
	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "parentCommentId", source = "parentComment", qualifiedByName = "parentCommentToParentCommentId")
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	public abstract CommentResponseDto toResponseDto(Comment comment);

	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "threadId", source = "thread.id")
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "repliesCount", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract CommentSummaryDto toSummaryDto(Comment comment);

	@Mapping(target = "thread", source = "threadId", qualifiedByName = "threadIdToThread")
	@Mapping(target = "user", expression = "java(userIdToUser(userId))")
	@Mapping(target = "parentComment", source = "parentCommentId", qualifiedByName = "parentCommentIdToParentComment")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "userCommentLikes", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Comment toEntity(CommentCreateDto createDto, @Context int userId);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "thread", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "parentComment", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "userCommentLikes", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void update(@MappingTarget Comment comment, CommentUpdateDto updateDto);

	@Named("parentCommentToParentCommentId")
	protected Integer parentCommentToParentCommentId(Comment parentComment) {
		return Optional.ofNullable(parentComment).map(Comment::getId).orElse(null);
	}

	@Named("threadIdToThread")
	protected ForumThread threadIdToThread(Integer threadId) {
		return forumThreadRepository.findById(threadId)
			.orElseThrow(() -> RecordNotFoundException.of(ForumThread.class, threadId));
	}

	@Named("userIdToUser")
	protected User userIdToUser(Integer userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	@Named("parentCommentIdToParentComment")
	protected Comment parentCommentIdToParentComment(Integer parentCommentId) {
		return Optional.ofNullable(parentCommentId)
			.map(id -> commentRepository.findById(id).orElseThrow(() -> RecordNotFoundException.of(Comment.class, id)))
			.orElse(null);
	}

}
