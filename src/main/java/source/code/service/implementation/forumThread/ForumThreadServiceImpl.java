package source.code.service.implementation.forumThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.helper.User.AuthorizationUtil;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.forum.ForumThread;
import source.code.repository.ForumThreadRepository;
import source.code.service.declaration.forumThread.ForumThreadService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

@Service
public class ForumThreadServiceImpl implements ForumThreadService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final ForumThreadMapper forumThreadMapper;
    private final RepositoryHelper repositoryHelper;
    private final ForumThreadRepository forumThreadRepository;

    public ForumThreadServiceImpl(JsonPatchService jsonPatchService,
                                  ValidationService validationService,
                                  ForumThreadMapper forumThreadMapper,
                                  RepositoryHelper repositoryHelper,
                                  ForumThreadRepository forumThreadRepository) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.forumThreadMapper = forumThreadMapper;
        this.repositoryHelper = repositoryHelper;
        this.forumThreadRepository = forumThreadRepository;
    }

    @Override
    @Transactional
    public ForumThreadResponseDto createForumThread(ForumThreadCreateDto createDto) {
        int userId = AuthorizationUtil.getUserId();
        ForumThread mapped = forumThreadMapper.toEntity(createDto, userId);
        ForumThread saved = forumThreadRepository.save(mapped);
        return forumThreadMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void updateForumThread(int threadId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        ForumThread thread = find(threadId);
        ForumThreadUpdateDto patched = applyPatchToForumThread(thread, patch);

        validationService.validate(patched);
        forumThreadMapper.update(thread, patched);
        ForumThread saved = forumThreadRepository.save(thread);
    }

    @Override
    @Transactional
    public void deleteForumThread(int threadId) {
        ForumThread thread = find(threadId);
        forumThreadRepository.delete(thread);
    }

    @Override
    public ForumThreadResponseDto getForumThread(int threadId) {
        ForumThread thread = find(threadId);
        return forumThreadMapper.toResponseDto(thread);
    }

    @Override
    public List<ForumThreadResponseDto> getAllForumThreads() {
        return forumThreadRepository.findAll().stream()
                .map(forumThreadMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ForumThreadResponseDto> getForumThreadsByCategory(int categoryId) {
        return forumThreadRepository.findAllByThreadCategoryId(categoryId).stream()
                .map(forumThreadMapper::toResponseDto)
                .toList();
    }

    private ForumThreadUpdateDto applyPatchToForumThread(
            ForumThread forumThread, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        ForumThreadResponseDto responseDto = forumThreadMapper.toResponseDto(forumThread);
        return jsonPatchService.applyPatch(patch, responseDto, ForumThreadUpdateDto.class);
    }

    private ForumThread find(int threadId) {
        return repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId);
    }

    public boolean isThreadOwnerOrAdmin(int threadId)  {
        ForumThread thread = find(threadId);
        return AuthorizationUtil.isOwnerOrAdmin(thread.getUser().getId());
    }
}
