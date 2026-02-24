package com.fitassist.backend.service.implementation.forumthread;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadUpdateDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.forumThread.ForumThreadMapper;
import com.fitassist.backend.mapper.forumThread.ForumThreadMappingContext;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.thread.ThreadCategory;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.ThreadCategoryRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.forumthread.ForumThreadService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.thread.ForumThreadPopulationService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.ForumThreadSpecification;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumThreadServiceImpl implements ForumThreadService {

	private final JsonPatchService jsonPatchService;

	private final ValidationService validationService;

	private final ForumThreadMapper forumThreadMapper;

	private final RepositoryHelper repositoryHelper;

	private final ForumThreadRepository forumThreadRepository;

	private final UserRepository userRepository;

	private final ThreadCategoryRepository threadCategoryRepository;

	private final SpecificationDependencies dependencies;

	private final ForumThreadPopulationService forumThreadPopulationService;

	public ForumThreadServiceImpl(JsonPatchService jsonPatchService, ValidationService validationService,
			ForumThreadMapper forumThreadMapper, RepositoryHelper repositoryHelper,
			ForumThreadRepository forumThreadRepository, UserRepository userRepository,
			ThreadCategoryRepository threadCategoryRepository, SpecificationDependencies dependencies,
			ForumThreadPopulationService forumThreadPopulationService) {
		this.jsonPatchService = jsonPatchService;
		this.validationService = validationService;
		this.forumThreadMapper = forumThreadMapper;
		this.repositoryHelper = repositoryHelper;
		this.forumThreadRepository = forumThreadRepository;
		this.userRepository = userRepository;
		this.threadCategoryRepository = threadCategoryRepository;
		this.dependencies = dependencies;
		this.forumThreadPopulationService = forumThreadPopulationService;
	}

	@Override
	@Transactional
	public ForumThreadResponseDto createForumThread(ForumThreadCreateDto createDto) {
		ForumThreadMappingContext context = prepareCreateContext(createDto);
		ForumThread mapped = forumThreadMapper.toEntity(createDto, context);
		ForumThread saved = forumThreadRepository.save(mapped);

		forumThreadRepository.flush();

		return findAndMap(saved.getId());
	}

	private ForumThreadMappingContext prepareCreateContext(ForumThreadCreateDto dto) {
		int userId = AuthorizationUtil.getUserId();
		User user = findUser(userId);
		ThreadCategory category = findCategory(dto.getThreadCategoryId());
		return new ForumThreadMappingContext(user, category);
	}

	private User findUser(int userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	@Override
	@Transactional
	public void updateForumThread(int threadId, JsonMergePatch patch) throws JacksonException {
		ForumThread thread = find(threadId);
		ForumThreadUpdateDto patched = applyPatchToForumThread(patch);

		validationService.validate(patched);

		ForumThreadMappingContext context = prepareUpdateContext(patched);
		forumThreadMapper.update(thread, patched, context);
		forumThreadRepository.save(thread);
	}

	private ForumThread find(int threadId) {
		return repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId);
	}

	private ForumThreadUpdateDto applyPatchToForumThread(JsonMergePatch patch) throws JacksonException {
		return jsonPatchService.createFromPatch(patch, ForumThreadUpdateDto.class);
	}

	private ForumThreadMappingContext prepareUpdateContext(ForumThreadUpdateDto dto) {
		ThreadCategory category = findCategory(dto.getThreadCategoryId());
		return new ForumThreadMappingContext(null, category);
	}

	private ThreadCategory findCategory(Integer categoryId) {
		if (categoryId == null) {
			return null;
		}

		return threadCategoryRepository.findById(categoryId)
			.orElseThrow(() -> RecordNotFoundException.of(ThreadCategory.class, categoryId));
	}

	@Override
	@Transactional
	public void deleteForumThread(int threadId) {
		ForumThread thread = find(threadId);
		forumThreadRepository.delete(thread);
	}

	@Override
	public ForumThreadResponseDto getForumThread(int threadId) {
		return findAndMap(threadId);
	}

	private ForumThreadResponseDto findAndMap(int threadId) {
		ForumThread thread = find(threadId);
		ForumThreadResponseDto responseDto = forumThreadMapper.toResponse(thread);
		forumThreadPopulationService.populate(responseDto);
		return responseDto;
	}

	@Override
	public Page<ForumThreadSummaryDto> getFilteredForumThreads(FilterDto filter, Pageable pageable) {
		SpecificationFactory<ForumThread> threadFactory = ForumThreadSpecification::new;
		SpecificationBuilder<ForumThread> specificationBuilder = SpecificationBuilder.of(filter, threadFactory,
				dependencies);
		Specification<ForumThread> specification = specificationBuilder.build();

		Page<ForumThread> threadPage = forumThreadRepository.findAll(specification, pageable);

		List<ForumThreadSummaryDto> summaries = threadPage.getContent()
			.stream()
			.map(forumThreadMapper::toSummary)
			.toList();

		forumThreadPopulationService.populate(summaries);

		return new PageImpl<>(summaries, pageable, threadPage.getTotalElements());
	}

}
