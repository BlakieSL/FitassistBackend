package source.code.service.implementation.forumThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.forum.ForumThread;
import source.code.repository.ForumThreadRepository;
import source.code.service.declaration.forumThread.ForumThreadService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.util.List;

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
        ForumThread thread = forumThreadRepository.save(forumThreadMapper.toEntity(createDto));
        return forumThreadMapper.toResponseDto(thread);
    }

    @Override
    @Transactional
    public void updateForumThread(int forumThreadId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        ForumThread thread = find(forumThreadId);
        ForumThreadUpdateDto patched = applyPatchToForumThread(thread, patch);

        validationService.validate(patched);
        forumThreadMapper.update(thread, patched);
        ForumThread saved = forumThreadRepository.save(thread);
    }

    @Override
    @Transactional
    public void deleteForumThread(int forumThreadId) {
        ForumThread thread = find(forumThreadId);
        forumThreadRepository.delete(thread);
    }

    @Override
    public ForumThreadResponseDto getForumThread(int forumThreadId) {
        ForumThread thread = find(forumThreadId);
        return forumThreadMapper.toResponseDto(thread);
    }

    @Override
    public List<ForumThreadResponseDto> getAllForumThreads() {
        return forumThreadRepository.findAll().stream()
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

    private ForumThread find(int forumThreadId) {
        return repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId);
    }
}
