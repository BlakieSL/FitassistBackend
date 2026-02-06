package com.fitassist.backend.mapper.forumThread;

import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.thread.ThreadCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ForumThreadMapper {

	@Mapping(target = "category", source = "threadCategory", qualifiedByName = "mapThreadCategoryToResponse")
	@Mapping(target = "author", source = "user", qualifiedByName = "mapUserToAuthorDto")
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "commentsCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ForumThreadResponseDto toResponse(ForumThread forumThread);

	@Mapping(target = "category", source = "threadCategory", qualifiedByName = "mapThreadCategoryToResponse")
	@Mapping(target = "author", source = "user", qualifiedByName = "mapUserToAuthorDto")
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "commentsCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract ForumThreadSummaryDto toSummary(ForumThread forumThread);

	@Mapping(target = "threadCategory", expression = "java(context.getCategory())")
	@Mapping(target = "user", expression = "java(context.getUser())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "userThreads", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	@Mapping(target = "complaints", ignore = true)
	public abstract ForumThread toEntity(ForumThreadCreateDto createDto, @Context ForumThreadMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "threadCategory", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "userThreads", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	@Mapping(target = "complaints", ignore = true)
	public abstract void update(@MappingTarget ForumThread forumThread, ForumThreadUpdateDto updateDto,
			@Context ForumThreadMappingContext context);

	@AfterMapping
	protected void updateCategory(@MappingTarget ForumThread forumThread, ForumThreadUpdateDto updateDto,
			@Context ForumThreadMappingContext context) {
		if (updateDto.getThreadCategoryId() != null) {
			forumThread.setThreadCategory(context.getCategory());
		}
	}

	@Named("mapThreadCategoryToResponse")
	protected CategoryResponseDto mapThreadCategoryToResponse(ThreadCategory threadCategory) {
		return new CategoryResponseDto(threadCategory.getId(), threadCategory.getName());
	}

}
