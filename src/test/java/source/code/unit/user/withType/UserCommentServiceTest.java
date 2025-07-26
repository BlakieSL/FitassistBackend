package source.code.unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserComment;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withType.UserCommentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCommentServiceTest {
    @Mock
    private UserCommentRepository userCommentRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    private UserCommentServiceImpl userCommentService;

    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);

        userCommentService = new UserCommentServiceImpl(
                userRepository,
                commentRepository,
                userCommentRepository,
                commentMapper
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
        int commentId = 100;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        User user = new User();
        Comment comment = new Comment();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.existsByUserIdAndCommentIdAndType(userId, commentId, type)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        userCommentService.saveToUser(commentId, type);

        verify(userCommentRepository).save(any(UserComment.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if type is SAVE")
    public void saveToUser_ShouldThrowNotSupportedInteractionTypeExceptionIfTypeIsSave() {
        int userId = 1;
        int commentId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.existsByUserIdAndCommentIdAndType(userId, commentId, type)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(new Comment()));

        assertThrows(NotSupportedInteractionTypeException.class, () -> userCommentService.saveToUser(commentId, type));

        verify(userCommentRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int commentId = 100;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.existsByUserIdAndCommentIdAndType(userId, commentId, type)).thenReturn(true);

        assertThrows(NotUniqueRecordException.class, () -> userCommentService.saveToUser(commentId, type));

        verify(userCommentRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int commentId = 100;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.existsByUserIdAndCommentIdAndType(userId, commentId, type)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.saveToUser(commentId, type));

        verify(userCommentRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if comment not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfCommentNotFound() {
        int userId = 1;
        int commentId = 100;
        User user = new User();
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.existsByUserIdAndCommentIdAndType(userId, commentId, type)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.saveToUser(commentId, type));

        verify(userCommentRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int commentId = 100;
        UserComment userCommentLike = new UserComment();
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.findByUserIdAndCommentIdAndType(userId, commentId, type))
                .thenReturn(Optional.of(userCommentLike));

        userCommentService.deleteFromUser(commentId, type);

        verify(userCommentRepository).delete(userCommentLike);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if user comment like not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserCommentLikeNotFound() {
        int userId = 1;
        int commentId = 100;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userCommentRepository.findByUserIdAndCommentIdAndType(userId, commentId, type))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.deleteFromUser(commentId, type));

        verify(userCommentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all liked comments from user")
    public void getAllFromUser_ShouldReturnAllLikedCommentsFromUser() {
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        int userId = 1;
        UserComment like1 = new UserComment();
        Comment comment1 = new Comment();
        like1.setComment(comment1);

        UserComment like2 = new UserComment();
        Comment comment2 = new Comment();
        like2.setComment(comment2);

        CommentResponseDto dto1 = new CommentResponseDto();
        CommentResponseDto dto2 = new CommentResponseDto();

        when(userCommentRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of(like1, like2));
        when(commentMapper.toResponseDto(comment1)).thenReturn(dto1);
        when(commentMapper.toResponseDto(comment2)).thenReturn(dto2);

        var result = userCommentService.getAllFromUser(userId, type);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no liked comments")
    public void getAllFromUser_ShouldReturnEmptyListIfNoLikedComments() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        when(userCommentRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userCommentService.getAllFromUser(userId, type);

        assertTrue(result.isEmpty());
        verify(commentMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should calculate likes and return 0 for saves")
    public void calculateLikesAndSaves_ShouldCalculateLikesAndReturnZeroForSaves() {
        int commentId = 100;
        int likeCount = 100;
        Comment comment = new Comment();
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userCommentRepository.countByCommentIdAndType(commentId, type)).thenReturn((long) likeCount);

        var result = userCommentService.calculateLikesAndSaves(commentId);


        assertEquals(likeCount, result.getLikes());
        assertEquals(0, result.getSaves());
        verify(commentRepository).findById(commentId);
        verify(userCommentRepository).countByCommentIdAndType(commentId, type);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if comment not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfCommentNotFound() {
        int commentId = 100;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.calculateLikesAndSaves(commentId));

        verify(userCommentRepository, never()).countByCommentIdAndType(anyInt(), any(TypeOfInteraction.class));
    }
}