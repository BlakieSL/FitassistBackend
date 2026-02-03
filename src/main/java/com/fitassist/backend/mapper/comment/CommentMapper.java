package com.fitassist.backend.mapper.comment;

import com.fitassist.backend.dto.request.comment.CommentCreateDto;
import com.fitassist.backend.dto.request.comment.CommentUpdateDto;
import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.thread.Comment;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class CommentMapper {

	@Mapping(target = "threadId", source = "thread.id")
	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "parentCommentId", source = "parentComment", qualifiedByName = "parentCommentToParentCommentId")
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "repliesCount", ignore = true)
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

	@Mapping(target = "thread", expression = "java(context.getThread())")
	@Mapping(target = "user", expression = "java(context.getUser())")
	@Mapping(target = "parentComment", expression = "java(context.getParentComment())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "userCommentLikes", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	@Mapping(target = "complaints", ignore = true)
	public abstract Comment toEntity(CommentCreateDto createDto, @Context CommentMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "thread", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "parentComment", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "userCommentLikes", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	@Mapping(target = "complaints", ignore = true)
	public abstract void update(@MappingTarget Comment comment, CommentUpdateDto updateDto);

	@Named("parentCommentToParentCommentId")
	protected Integer parentCommentToParentCommentId(Comment parentComment) {
		return Optional.ofNullable(parentComment).map(Comment::getId).orElse(null);
	}

}
