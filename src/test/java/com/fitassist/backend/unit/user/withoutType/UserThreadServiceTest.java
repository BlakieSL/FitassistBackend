package com.fitassist.backend.unit.user.withoutType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.forumThread.ForumThreadMapper;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.UserThread;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.repository.UserThreadRepository;
import com.fitassist.backend.service.declaration.thread.ForumThreadPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withoutType.UserThreadImplService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserThreadServiceTest {

	private static final int USER_ID = 1;

	private static final int THREAD_ID = 100;

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

	private UserThreadImplService userThreadService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	private User user;

	private ForumThread forumThread;

	private UserThread userThread;

	@BeforeEach
	void setUp() {
		userThreadService = new UserThreadImplService(userThreadRepository, forumThreadRepository, userRepository,
				forumThreadMapper, forumThreadPopulationService);
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		forumThread = new ForumThread();
		forumThread.setId(THREAD_ID);
		forumThread.setUser(user);
		userThread = UserThread.of(user, forumThread);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUser() {
		when(userThreadRepository.existsByUserIdAndForumThreadId(USER_ID, THREAD_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(THREAD_ID)).thenReturn(Optional.of(forumThread));

		userThreadService.saveToUser(THREAD_ID);

		verify(userThreadRepository).save(any(UserThread.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		when(userThreadRepository.existsByUserIdAndForumThreadId(USER_ID, THREAD_ID)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userThreadService.saveToUser(THREAD_ID));

		verify(userThreadRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		when(userThreadRepository.existsByUserIdAndForumThreadId(USER_ID, THREAD_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userThreadService.saveToUser(THREAD_ID));

		verify(userThreadRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfThreadNotFound() {
		when(userThreadRepository.existsByUserIdAndForumThreadId(USER_ID, THREAD_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(THREAD_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userThreadService.saveToUser(THREAD_ID));

		verify(userThreadRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		when(userThreadRepository.findByUserIdAndForumThreadId(USER_ID, THREAD_ID)).thenReturn(Optional.of(userThread));

		userThreadService.deleteFromUser(THREAD_ID);

		verify(userThreadRepository).delete(userThread);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserThreadNotFound() {
		when(userThreadRepository.findByUserIdAndForumThreadId(USER_ID, THREAD_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userThreadService.deleteFromUser(THREAD_ID));

		verify(userThreadRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnPagedThreads() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		ForumThread thread2 = new ForumThread();
		thread2.setId(2);
		thread2.setUser(user);
		UserThread ut2 = UserThread.of(user, thread2);

		ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
		dto1.setId(THREAD_ID);
		ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();
		dto2.setId(2);

		Page<UserThread> userThreadPage = new PageImpl<>(List.of(userThread, ut2), pageable, 2);

		when(userThreadRepository.findAllByUserId(eq(USER_ID), eq(pageable))).thenReturn(userThreadPage);
		when(forumThreadMapper.toSummary(forumThread)).thenReturn(dto1);
		when(forumThreadMapper.toSummary(thread2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userThreadService.getAllFromUser(USER_ID, pageable);

		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		verify(forumThreadMapper, times(2)).toSummary(any(ForumThread.class));
		verify(forumThreadPopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyPageIfNoThreads() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserThread> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userThreadRepository.findAllByUserId(eq(USER_ID), eq(pageable))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userThreadService.getAllFromUser(USER_ID, pageable);

		assertTrue(result.isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
