package source.code.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;
import source.code.model.user.UserThread;
import source.code.repository.ForumThreadRepository;
import source.code.repository.MediaRepository;
import source.code.repository.UserRepository;
import source.code.repository.UserThreadRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.user.interaction.withoutType.UserThreadServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class  UserThreadServiceTest {
    @Mock
    private UserThreadRepository userThreadRepository;
    @Mock
    private ForumThreadRepository forumThreadRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ForumThreadMapper forumThreadMapper;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private AwsS3Service awsS3Service;
    private UserThreadServiceImpl userThreadService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);

        userThreadService = new UserThreadServiceImpl(
                userThreadRepository,
                forumThreadRepository,
                userRepository,
                forumThreadMapper,
                mediaRepository,
                awsS3Service
        );
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("saveToUser - Should save to user")
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
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int threadId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userThreadRepository.existsByUserIdAndForumThreadId(userId, threadId)).thenReturn(true);

        assertThrows(NotUniqueRecordException.class, () -> userThreadService.saveToUser(threadId));

        verify(userThreadRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
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
    @DisplayName("saveToUser - Should throw exception if thread not found")
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
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int threadId = 100;
        UserThread subscription = new UserThread();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userThreadRepository.findByUserIdAndForumThreadId(userId, threadId))
                .thenReturn(Optional.of(subscription));

        userThreadService.deleteFromUser(threadId);

        verify(userThreadRepository).delete(subscription);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if subscription not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfSubscriptionNotFound() {
        int userId = 1;
        int threadId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userThreadRepository.findByUserIdAndForumThreadId(userId, threadId))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userThreadService.deleteFromUser(threadId));

        verify(userThreadRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all saved threads from user")
    public void getAllFromUser_ShouldReturnAllSavedThreadsFromUser() {
        int userId = 1;
        UserThread sub1 = new UserThread();
        ForumThread thread1 = new ForumThread();
        sub1.setForumThread(thread1);

        UserThread sub2 = new UserThread();
        ForumThread thread2 = new ForumThread();
        sub2.setForumThread(thread2);

        ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
        ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();

        when(userThreadRepository.findAllByUserId(userId))
                .thenReturn(List.of(sub1, sub2));
        when(forumThreadMapper.toSummaryDto(thread1)).thenReturn(dto1);
        when(forumThreadMapper.toSummaryDto(thread2)).thenReturn(dto2);
        when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(anyInt(), any()))
                .thenReturn(Optional.empty());

        var result = userThreadService.getAllFromUser(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no saved threads")
    public void getAllFromUser_ShouldReturnEmptyListIfNoSavedThreads() {
        int userId = 1;

        when(userThreadRepository.findAllByUserId(userId))
                .thenReturn(List.of());

        var result = userThreadService.getAllFromUser(userId);

        assertTrue(result.isEmpty());
        verify(forumThreadMapper, never()).toSummaryDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return 0 for both likes and saves")
    public void calculateLikesAndSaves_ShouldReturnZeroForBoth() {
        int threadId = 100;
        ForumThread thread = new ForumThread();

        when(forumThreadRepository.findById(threadId)).thenReturn(Optional.of(thread));

        var result = userThreadService.calculateLikesAndSaves(threadId);

        assertEquals(0, result.getLikes());
        assertEquals(0, result.getSaves());
        verify(forumThreadRepository).findById(threadId);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if thread not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfThreadNotFound() {
        int threadId = 100;

        when(forumThreadRepository.findById(threadId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userThreadService.calculateLikesAndSaves(threadId));
    }
}