package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.helper.CommonMappingHelper;
import source.code.model.thread.ForumThread;
import source.code.model.thread.ThreadCategory;
import source.code.model.user.User;
import source.code.repository.ThreadCategoryRepository;
import source.code.repository.UserRepository;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ForumThreadMapper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ThreadCategoryRepository threadCategoryRepository;

	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "commentsCount", ignore = true)
	@Mapping(target = "category", source = "threadCategory", qualifiedByName = "threadCategoryToCategoryResponseDto")
	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "saved", ignore = true)
	public abstract ForumThreadResponseDto toResponseDto(ForumThread forumThread);

	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "commentsCount", ignore = true)
	@Mapping(target = "category", source = "threadCategory", qualifiedByName = "threadCategoryToCategoryResponseDto")
	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract ForumThreadSummaryDto toSummaryDto(ForumThread forumThread);

	@Mapping(target = "user", expression = "java(userIdToUser(userId))")
	@Mapping(target = "threadCategory", source = "threadCategoryId",
			qualifiedByName = "threadCategoryIdToThreadCategory")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "userThreads", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract ForumThread toEntity(ForumThreadCreateDto createDto, @Context int userId);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "threadCategory", source = "threadCategoryId",
			qualifiedByName = "threadCategoryIdToThreadCategory")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "userThreads", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void update(@MappingTarget ForumThread forumThread, ForumThreadUpdateDto updateDto);

	@Named("threadCategoryToCategoryResponseDto")
	protected CategoryResponseDto threadCategoryToCategoryResponseDto(ThreadCategory threadCategory) {
		return new CategoryResponseDto(threadCategory.getId(), threadCategory.getName());
	}

	@Named("userIdToUser")
	protected User userIdToUser(Integer userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	@Named("threadCategoryIdToThreadCategory")
	protected ThreadCategory threadCategoryIdToThreadCategory(Integer threadCategoryId) {
		return threadCategoryRepository.findById(threadCategoryId)
			.orElseThrow(() -> RecordNotFoundException.of(ThreadCategory.class, threadCategoryId));
	}

}
