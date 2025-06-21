package source.code.unit.forumThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.request.forumThread.ForumThreadUpdateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.thread.ForumThread;
import source.code.repository.ForumThreadRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.forumThread.ForumThreadServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForumThreadServiceTest {
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private ForumThreadMapper forumThreadMapper;
    @Mock
    private ValidationService validationService;
    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ForumThreadRepository forumThreadRepository;
    @InjectMocks
    private ForumThreadServiceImpl forumThreadService;

    private ForumThread forumThread;
    private ForumThreadCreateDto createDto;
    private ForumThreadResponseDto responseDto;
    private JsonMergePatch patch;
    private ForumThreadUpdateDto patchedDto;
    private int threadId;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        forumThread = new ForumThread();
        createDto = new ForumThreadCreateDto();
        responseDto = new ForumThreadResponseDto();
        patchedDto = new ForumThreadUpdateDto();
        threadId = 1;
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void createForumThread_shouldCreateForumThread() {
        int userId = 1;
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(forumThreadMapper.toEntity(createDto, userId)).thenReturn(forumThread);
        when(forumThreadRepository.save(forumThread)).thenReturn(forumThread);
        when(forumThreadMapper.toResponseDto(forumThread)).thenReturn(responseDto);

        ForumThreadResponseDto result = forumThreadService.createForumThread(createDto);

        assertEquals(responseDto, result);
    }

    @Test
    void updateForumThread_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenReturn(forumThread);
        when(forumThreadMapper.toResponseDto(forumThread)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, ForumThreadUpdateDto.class))
                .thenReturn(patchedDto);
        when(forumThreadRepository.save(forumThread)).thenReturn(forumThread);

        forumThreadService.updateForumThread(threadId, patch);

        verify(validationService).validate(patchedDto);
        verify(forumThreadMapper).update(forumThread, patchedDto);
        verify(forumThreadRepository).save(forumThread);
    }

    @Test
    void updateForumThread_shouldThrowExceptionWhenThreadNotFound() {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenThrow(RecordNotFoundException.of(ForumThread.class, threadId));

        assertThrows(RecordNotFoundException.class,
                () -> forumThreadService.updateForumThread(threadId, patch)
        );

        verifyNoInteractions(forumThreadMapper, jsonPatchService, validationService);
        verify(forumThreadRepository, never()).save(forumThread);
    }

    @Test
    void updateForumThread_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenReturn(forumThread);
        when(forumThreadMapper.toResponseDto(forumThread)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, ForumThreadUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () ->
                forumThreadService.updateForumThread(threadId, patch)
        );
        verify(validationService).validate(patchedDto);
        verify(forumThreadRepository, never()).save(forumThread);
    }

    @Test
    void deleteForumThread_shouldDelete() {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenReturn(forumThread);

        forumThreadService.deleteForumThread(threadId);

        verify(forumThreadRepository).delete(forumThread);
    }

    @Test
    void deleteForumThread_shouldThrowExceptionWhenThreadNotFound() {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenThrow(RecordNotFoundException.of(ForumThread.class, threadId));

        assertThrows(RecordNotFoundException.class,
                () -> forumThreadService.deleteForumThread(threadId)
        );

        verify(forumThreadRepository, never()).delete(forumThread);
    }

    @Test
    void getForumThread_shouldReturnForumThreadWhenFound() {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenReturn(forumThread);
        when(forumThreadMapper.toResponseDto(forumThread)).thenReturn(responseDto);

        ForumThreadResponseDto result = forumThreadService.getForumThread(threadId);

        assertEquals(responseDto, result);
    }

    @Test
    void getForumThread_shouldThrowExceptionWhenThreadNotFound() {
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
                .thenThrow(RecordNotFoundException.of(ForumThread.class, threadId));

        assertThrows(RecordNotFoundException.class,
                () -> forumThreadService.getForumThread(threadId)
        );

        verifyNoInteractions(forumThreadMapper);
    }

    @Test
    void getAllForumThreads_shouldReturnAllForumThreads() {
        List<ForumThreadResponseDto> responseDtos = List.of(responseDto);

        when(forumThreadRepository.findAll()).thenReturn(List.of(forumThread));
        when(forumThreadMapper.toResponseDto(forumThread)).thenReturn(responseDto);

        List<ForumThreadResponseDto> result = forumThreadService.getAllForumThreads();

        assertEquals(responseDtos, result);
    }

    @Test
    void getAllForumThreads_shouldReturnEmptyListWhenNoThreads() {
        when(forumThreadRepository.findAll()).thenReturn(List.of());

        List<ForumThreadResponseDto> result = forumThreadService.getAllForumThreads();

        assertTrue(result.isEmpty());
    }

    @Test
    void getForumThreadsByCategory_shouldReturnThreadsForCategory() {
        int categoryId = 1;
        when(forumThreadRepository.findAllByThreadCategoryId(categoryId))
                .thenReturn(List.of(forumThread));
        when(forumThreadMapper.toResponseDto(forumThread)).thenReturn(responseDto);

        List<ForumThreadResponseDto> result = forumThreadService
                .getForumThreadsByCategory(categoryId);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
    }

    @Test
    void getForumThreadsByCategory_shouldReturnEmptyListWhenNoThreads() {
        int categoryId = 1;
        when(forumThreadRepository.findAllByThreadCategoryId(categoryId)).thenReturn(List.of());

        List<ForumThreadResponseDto> result = forumThreadService.
                getForumThreadsByCategory(categoryId);

        assertTrue(result.isEmpty());
    }
}