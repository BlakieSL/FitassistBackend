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
import org.springframework.data.domain.Sort;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserComment;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.user.interaction.withType.UserCommentServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Mock
    private AwsS3Service awsS3Service;

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
        CommentSummaryDto dto1 = new CommentSummaryDto();
        dto1.setId(1);
        CommentSummaryDto dto2 = new CommentSummaryDto();
        dto2.setId(2);

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(List.of(dto1, dto2));

        var result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertEquals(2, result.size());
        verify(userCommentRepository).findCommentSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no liked comments")
    public void getAllFromUser_ShouldReturnEmptyListIfNoLikedComments() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertTrue(result.isEmpty());
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

    @Test
    @DisplayName("getAllFromUser with type and sortDirection DESC - Should sort by interaction date DESC")
    public void getAllFromUser_WithType_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
        verify(userCommentRepository).findCommentSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type and sortDirection ASC - Should sort by interaction date ASC")
    public void getAllFromUser_WithType_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.DISLIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto2, dto1)));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.ASC);

        assertSortedResult(result, 2, 1, 2);
        verify(userCommentRepository).findCommentSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type default - Should sort DESC when no direction specified")
    public void getAllFromUser_WithType_DefaultShouldSortDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
        verify(userCommentRepository).findCommentSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type - Should handle null dates properly")
    public void getAllFromUser_WithType_ShouldHandleNullDates() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        CommentSummaryDto dto1 = createCommentSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        CommentSummaryDto dto2 = createCommentSummaryDto(2, null);
        CommentSummaryDto dto3 = createCommentSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2, dto3)));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertSortedResult(result, 3, 3, 1, 2);
        verify(userCommentRepository).findCommentSummaryByUserIdAndType(userId, type);
    }

    @Test
    @DisplayName("getAllFromUser with type - Should populate author image URLs after sorting")
    public void getAllFromUser_WithType_ShouldPopulateAuthorImageUrlsAfterSorting() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        dto1.setAuthorImageUrl("author1.jpg");
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);
        dto2.setAuthorImageUrl("author2.jpg");

        when(userCommentRepository.findCommentSummaryByUserIdAndType(userId, type))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));
        when(awsS3Service.getImage("author1.jpg")).thenReturn("https://s3.com/author1.jpg");
        when(awsS3Service.getImage("author2.jpg")).thenReturn("https://s3.com/author2.jpg");

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        CommentSummaryDto first = (CommentSummaryDto) result.get(0);
        CommentSummaryDto second = (CommentSummaryDto) result.get(1);
        assertEquals("https://s3.com/author2.jpg", first.getAuthorImageUrl());
        assertEquals("https://s3.com/author1.jpg", second.getAuthorImageUrl());
        verify(awsS3Service).getImage("author1.jpg");
        verify(awsS3Service).getImage("author2.jpg");
    }

    private CommentSummaryDto createCommentSummaryDto(int id, LocalDateTime interactionDate) {
        CommentSummaryDto dto = new CommentSummaryDto();
        dto.setId(id);
        dto.setUserCommentInteractionCreatedAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((CommentSummaryDto) result.get(i)).getId());
        }
    }
}