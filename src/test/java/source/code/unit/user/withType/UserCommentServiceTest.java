package source.code.unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.CommentMapper;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserComment;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.comment.CommentPopulationService;
import source.code.service.implementation.user.interaction.withType.UserCommentServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private CommentPopulationService commentPopulationService;

    private UserCommentServiceImpl userCommentService;

    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
        userCommentService = new UserCommentServiceImpl(
                userRepository,
                commentRepository,
                userCommentRepository,
                commentMapper,
                commentPopulationService
        );
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
    public void getAllFromUser_ShouldReturnAllLikedCommentsFromUser() {
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        int userId = 1;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        User user = new User();
        user.setId(1);

        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setUser(user);
        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setUser(user);

        UserComment uc1 = new UserComment();
        uc1.setComment(comment1);
        uc1.setCreatedAt(LocalDateTime.now());
        UserComment uc2 = new UserComment();
        uc2.setComment(comment2);
        uc2.setCreatedAt(LocalDateTime.now());

        CommentSummaryDto dto1 = new CommentSummaryDto();
        dto1.setId(1);
        CommentSummaryDto dto2 = new CommentSummaryDto();
        dto2.setId(2);

        Page<UserComment> userCommentPage = new PageImpl<>(List.of(uc1, uc2), pageable, 2);
        when(userCommentRepository.findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class)))
                .thenReturn(userCommentPage);
        when(commentMapper.toSummaryDto(comment1)).thenReturn(dto1);
        when(commentMapper.toSummaryDto(comment2)).thenReturn(dto2);

        Page<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(userCommentRepository).findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class));
        verify(commentMapper).toSummaryDto(comment1);
        verify(commentMapper).toSummaryDto(comment2);
        verify(commentPopulationService).populate(any(List.class));
    }

    @Test
    public void getAllFromUser_ShouldReturnEmptyListIfNoLikedComments() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserComment> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(userCommentRepository.findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(userCommentRepository).findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class));
    }
}
