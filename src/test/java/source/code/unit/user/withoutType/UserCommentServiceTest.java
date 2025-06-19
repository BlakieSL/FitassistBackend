package source.code.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.model.forum.Comment;
import source.code.model.user.UserCommentLikes;
import source.code.model.user.User;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentLikesRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withoutType.UserCommentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCommentServiceTest {
    @Mock
    private UserCommentLikesRepository userCommentLikesRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private UserCommentServiceImpl userCommentService;

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
    @DisplayName("saveToUser - Should save to user")
    public void saveToUser_ShouldSaveToUser() {
        int userId = 1;
        int commentId = 100;
        User user = new User();
        Comment comment = new Comment();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        userCommentService.saveToUser(commentId);

        verify(userCommentLikesRepository).save(any(UserCommentLikes.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int commentId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(true);

        assertThrows(NotUniqueRecordException.class, () -> userCommentService.saveToUser(commentId));

        verify(userCommentLikesRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int commentId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.saveToUser(commentId));

        verify(userCommentLikesRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if comment not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfCommentNotFound() {
        int userId = 1;
        int commentId = 100;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.saveToUser(commentId));

        verify(userCommentLikesRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int commentId = 100;
        UserCommentLikes userCommentLike = new UserCommentLikes();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.findByUserIdAndCommentId(userId, commentId))
                .thenReturn(Optional.of(userCommentLike));

        userCommentService.deleteFromUser(commentId);

        verify(userCommentLikesRepository).delete(userCommentLike);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if user comment like not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserCommentLikeNotFound() {
        int userId = 1;
        int commentId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.findByUserIdAndCommentId(userId, commentId))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.deleteFromUser(commentId));

        verify(userCommentLikesRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all liked comments from user")
    public void getAllFromUser_ShouldReturnAllLikedCommentsFromUser() {
        int userId = 1;
        UserCommentLikes like1 = new UserCommentLikes();
        Comment comment1 = new Comment();
        like1.setComment(comment1);

        UserCommentLikes like2 = new UserCommentLikes();
        Comment comment2 = new Comment();
        like2.setComment(comment2);

        CommentResponseDto dto1 = new CommentResponseDto();
        CommentResponseDto dto2 = new CommentResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.findAllByUserId(userId))
                .thenReturn(List.of(like1, like2));
        when(commentMapper.toResponseDto(comment1)).thenReturn(dto1);
        when(commentMapper.toResponseDto(comment2)).thenReturn(dto2);

        var result = userCommentService.getAllFromUser();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no liked comments")
    public void getAllFromUser_ShouldReturnEmptyListIfNoLikedComments() {
        int userId = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentLikesRepository.findAllByUserId(userId))
                .thenReturn(List.of());

        var result = userCommentService.getAllFromUser();

        assertTrue(result.isEmpty());
        verify(commentMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should calculate likes and return 0 for saves")
    public void calculateLikesAndSaves_ShouldCalculateLikesAndReturnZeroForSaves() {
        int commentId = 100;
        int likeCount = 100;
        Comment comment = new Comment();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userCommentLikesRepository.countAllByCommentId(commentId)).thenReturn((long) likeCount);

        var result = userCommentService.calculateLikesAndSaves(commentId);


        assertEquals(likeCount, result.getLikes());
        assertEquals(0, result.getSaves());
        verify(commentRepository).findById(commentId);
        verify(userCommentLikesRepository).countAllByCommentId(commentId);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if comment not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfCommentNotFound() {
        int commentId = 100;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.calculateLikesAndSaves(commentId));

        verify(userCommentLikesRepository, never()).countAllByCommentId(anyInt());
    }
}