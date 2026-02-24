package com.fitassist.backend.unit.forumThread;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadUpdateDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.forumThread.ForumThreadMapper;
import com.fitassist.backend.mapper.forumThread.ForumThreadMappingContext;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.thread.ThreadCategory;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.ThreadCategoryRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.thread.ForumThreadPopulationService;
import com.fitassist.backend.service.implementation.forumthread.ForumThreadServiceImpl;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import jakarta.json.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

	@Mock
	private UserRepository userRepository;

	@Mock
	private ThreadCategoryRepository threadCategoryRepository;

	@Mock
	private SpecificationDependencies dependencies;

	@Mock
	private ForumThreadPopulationService forumThreadPopulationService;

	@InjectMocks
	private ForumThreadServiceImpl forumThreadService;

	private ForumThread forumThread;

	private ForumThreadCreateDto createDto;

	private ForumThreadResponseDto responseDto;

	private JsonMergePatch patch;

	private ForumThreadUpdateDto patchedDto;

	private int threadId;

	private int userId;

	private int categoryId;

	private User user;

	private ThreadCategory threadCategory;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		forumThread = new ForumThread();
		createDto = new ForumThreadCreateDto();
		responseDto = new ForumThreadResponseDto();
		patchedDto = new ForumThreadUpdateDto();
		threadId = 1;
		userId = 1;
		categoryId = 1;
		user = new User();
		threadCategory = new ThreadCategory();
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		createDto.setThreadCategoryId(categoryId);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createForumThread_shouldCreateForumThread() {
		forumThread.setId(threadId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(threadCategoryRepository.findById(categoryId)).thenReturn(Optional.of(threadCategory));
		when(forumThreadMapper.toEntity(eq(createDto), any(ForumThreadMappingContext.class))).thenReturn(forumThread);
		when(forumThreadRepository.save(forumThread)).thenReturn(forumThread);
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId)).thenReturn(forumThread);
		when(forumThreadMapper.toResponse(forumThread)).thenReturn(responseDto);

		ForumThreadResponseDto result = forumThreadService.createForumThread(createDto);

		assertEquals(responseDto, result);
		verify(forumThreadPopulationService).populate(responseDto);
	}

	@Test
	void createForumThread_shouldThrowExceptionWhenUserNotFound() {
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> forumThreadService.createForumThread(createDto));

		verify(forumThreadRepository, never()).save(any());
	}

	@Test
	void createForumThread_shouldThrowExceptionWhenCategoryNotFound() {
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(threadCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> forumThreadService.createForumThread(createDto));

		verify(forumThreadRepository, never()).save(any());
	}

	@Test
	void updateForumThread_shouldUpdate() throws JacksonException {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId)).thenReturn(forumThread);
		when(jsonPatchService.createFromPatch(patch, ForumThreadUpdateDto.class)).thenReturn(patchedDto);
		when(forumThreadRepository.save(forumThread)).thenReturn(forumThread);

		forumThreadService.updateForumThread(threadId, patch);

		verify(validationService).validate(patchedDto);
		verify(forumThreadMapper).update(eq(forumThread), eq(patchedDto), any(ForumThreadMappingContext.class));
		verify(forumThreadRepository).save(forumThread);
	}

	@Test
	void updateForumThread_shouldThrowExceptionWhenThreadNotFound() {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
			.thenThrow(RecordNotFoundException.of(ForumThread.class, threadId));

		assertThrows(RecordNotFoundException.class, () -> forumThreadService.updateForumThread(threadId, patch));

		verifyNoInteractions(forumThreadMapper, jsonPatchService, validationService);
		verify(forumThreadRepository, never()).save(forumThread);
	}

	@Test
	void updateForumThread_shouldThrowExceptionWhenValidationFails() throws JacksonException {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId)).thenReturn(forumThread);
		when(jsonPatchService.createFromPatch(patch, ForumThreadUpdateDto.class)).thenReturn(patchedDto);

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> forumThreadService.updateForumThread(threadId, patch));
		verify(validationService).validate(patchedDto);
		verify(forumThreadRepository, never()).save(forumThread);
	}

	@Test
	void deleteForumThread_shouldDelete() {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId)).thenReturn(forumThread);

		forumThreadService.deleteForumThread(threadId);

		verify(forumThreadRepository).delete(forumThread);
	}

	@Test
	void deleteForumThread_shouldThrowExceptionWhenThreadNotFound() {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
			.thenThrow(RecordNotFoundException.of(ForumThread.class, threadId));

		assertThrows(RecordNotFoundException.class, () -> forumThreadService.deleteForumThread(threadId));

		verify(forumThreadRepository, never()).delete(forumThread);
	}

	@Test
	void getForumThread_shouldReturnForumThreadWhenFound() {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId)).thenReturn(forumThread);
		when(forumThreadMapper.toResponse(forumThread)).thenReturn(responseDto);

		ForumThreadResponseDto result = forumThreadService.getForumThread(threadId);

		assertEquals(responseDto, result);
		verify(forumThreadPopulationService).populate(responseDto);
	}

	@Test
	void getForumThread_shouldThrowExceptionWhenThreadNotFound() {
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, threadId))
			.thenThrow(RecordNotFoundException.of(ForumThread.class, threadId));

		assertThrows(RecordNotFoundException.class, () -> forumThreadService.getForumThread(threadId));

		verifyNoInteractions(forumThreadMapper);
	}

}
