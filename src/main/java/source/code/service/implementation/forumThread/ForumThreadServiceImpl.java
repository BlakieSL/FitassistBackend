package source.code.service.implementation.forumThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.ForumThreadMapper;
import source.code.model.thread.ForumThread;
import source.code.repository.ForumThreadRepository;
import source.code.service.declaration.forumThread.ForumThreadService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.thread.ForumThreadPopulationService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ForumThreadSpecification;

import java.util.List;

@Service
public class ForumThreadServiceImpl implements ForumThreadService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final ForumThreadMapper forumThreadMapper;
    private final RepositoryHelper repositoryHelper;
    private final ForumThreadRepository forumThreadRepository;
    private final SpecificationDependencies dependencies;
    private final ForumThreadPopulationService forumThreadPopulationService;

    public ForumThreadServiceImpl(JsonPatchService jsonPatchService,
                                  ValidationService validationService,
                                  ForumThreadMapper forumThreadMapper,
                                  RepositoryHelper repositoryHelper,
                                  ForumThreadRepository forumThreadRepository,
                                  SpecificationDependencies dependencies,
                                  ForumThreadPopulationService forumThreadPopulationService) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.forumThreadMapper = forumThreadMapper;
        this.repositoryHelper = repositoryHelper;
        this.forumThreadRepository = forumThreadRepository;
        this.dependencies = dependencies;
        this.forumThreadPopulationService = forumThreadPopulationService;
    }

    @Override
    @Transactional
    public ForumThreadResponseDto createForumThread(ForumThreadCreateDto createDto) {
        int userId = AuthorizationUtil.getUserId();
        ForumThread mapped = forumThreadMapper.toEntity(createDto, userId);
        ForumThread saved = forumThreadRepository.save(mapped);

        forumThreadRepository.flush();

        return findAndMap(saved.getId());
    }

    @Override
    @Transactional
    public void updateForumThread(int threadId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        ForumThread thread = find(threadId);
        ForumThreadUpdateDto patched = applyPatchToForumThread(patch);

        validationService.validate(patched);
        forumThreadMapper.update(thread, patched);
        forumThreadRepository.save(thread);
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

    private ForumThreadUpdateDto applyPatchToForumThread(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return jsonPatchService.createFromPatch(patch, ForumThreadUpdateDto.class);
    }

    private ForumThread find(int threadId) {
        return repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId);
    }

    private ForumThreadResponseDto findAndMap(int threadId) {
        ForumThread thread = find(threadId);
        ForumThreadResponseDto responseDto = forumThreadMapper.toResponseDto(thread);
        forumThreadPopulationService.populate(responseDto);
        return responseDto;
    }

    @Override
    public Page<ForumThreadSummaryDto> getFilteredForumThreads(FilterDto filter, Pageable pageable) {
        SpecificationFactory<ForumThread> threadFactory = ForumThreadSpecification::new;
        SpecificationBuilder<ForumThread> specificationBuilder = SpecificationBuilder.of(filter, threadFactory, dependencies);
        Specification<ForumThread> specification = specificationBuilder.build();

        Page<ForumThread> threadPage = forumThreadRepository.findAll(specification, pageable);

        List<ForumThreadSummaryDto> summaries = threadPage.getContent().stream()
                .map(forumThreadMapper::toSummaryDto)
                .toList();

        forumThreadPopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, threadPage.getTotalElements());
    }
}
