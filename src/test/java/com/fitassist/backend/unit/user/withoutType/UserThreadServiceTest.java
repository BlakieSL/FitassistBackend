package com.fitassist.backend.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.mapper.ForumThreadMapper;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserThread;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.repository.UserThreadRepository;
import com.fitassist.backend.service.declaration.thread.ForumThreadPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withoutType.UserThreadServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserThreadServiceTest {

	@Mock
	private UserThreadRepository userThreadRepository;

	@Mock
	private ForumThreadRepository forumThreadRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ForumThreadMapper forumThreadMapper;

	@Mock
	private ForumThreadPopulationService forumThreadPopulationService;

	@InjectMocks
	private UserThreadServiceImpl userThreadService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	@BeforeEach
	void setUp() {
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUser() {
		int userId = 1;
		int threadId = 100;
		User user = new User();
		ForumThread thread = new ForumThread();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userThreadRepository.existsByUserIdAndForumThreadId(userId, threadId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(threadId)).thenReturn(Optional.of(thread));

		userThreadService.saveToUser(threadId);

		verify(userThreadRepository).save(any(UserThread.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		int userId = 1;
		int threadId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userThreadRepository.existsByUserIdAndForumThreadId(userId, threadId)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userThreadService.saveToUser(threadId));

		verify(userThreadRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		int userId = 1;
		int threadId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userThreadRepository.existsByUserIdAndForumThreadId(userId, threadId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userThreadService.saveToUser(threadId));

		verify(userThreadRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfThreadNotFound() {
		int userId = 1;
		int threadId = 100;
		User user = new User();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userThreadRepository.existsByUserIdAndForumThreadId(userId, threadId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(threadId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userThreadService.saveToUser(threadId));

		verify(userThreadRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		int userId = 1;
		int threadId = 100;
		UserThread subscription = new UserThread();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userThreadRepository.findByUserIdAndForumThreadId(userId, threadId)).thenReturn(Optional.of(subscription));

		userThreadService.deleteFromUser(threadId);

		verify(userThreadRepository).delete(subscription);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfSubscriptionNotFound() {
		int userId = 1;
		int threadId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userThreadRepository.findByUserIdAndForumThreadId(userId, threadId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userThreadService.deleteFromUser(threadId));

		verify(userThreadRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllSavedThreadsFromUser() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		User user = new User();
		user.setId(1);

		ForumThread thread1 = new ForumThread();
		thread1.setId(1);
		thread1.setUser(user);
		ForumThread thread2 = new ForumThread();
		thread2.setId(2);
		thread2.setUser(user);

		UserThread ut1 = new UserThread();
		ut1.setForumThread(thread1);
		ut1.setCreatedAt(LocalDateTime.now());
		UserThread ut2 = new UserThread();
		ut2.setForumThread(thread2);
		ut2.setCreatedAt(LocalDateTime.now());

		ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
		dto1.setId(1);
		ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();
		dto2.setId(2);

		Page<UserThread> userThreadPage = new PageImpl<>(List.of(ut1, ut2), pageable, 2);
		when(userThreadRepository.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(userThreadPage);
		when(forumThreadMapper.toSummaryDto(thread1)).thenReturn(dto1);
		when(forumThreadMapper.toSummaryDto(thread2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userThreadService.getAllFromUser(userId, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(userThreadRepository).findAllByUserId(eq(userId), any(Pageable.class));
		verify(forumThreadMapper).toSummaryDto(thread1);
		verify(forumThreadMapper).toSummaryDto(thread2);
		verify(forumThreadPopulationService).populate(any(List.class));
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoSavedThreads() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<UserThread> emptyPage = new PageImpl<>(List.of(), pageable, 0);
		when(userThreadRepository.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userThreadService.getAllFromUser(userId, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
		verify(userThreadRepository).findAllByUserId(eq(userId), any(Pageable.class));
	}

}
