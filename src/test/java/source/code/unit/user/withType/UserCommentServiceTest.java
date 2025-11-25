package source.code.unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.implementation.user.interaction.withType.UserCommentServiceImpl;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    private ImageUrlPopulationService imageUrlPopulationService;

    @Mock
    private SortingService sortingService;

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
                imageUrlPopulationService,
                sortingService
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
        CommentSummaryDto dto1 = new CommentSummaryDto();
        dto1.setId(1);
        CommentSummaryDto dto2 = new CommentSummaryDto();
        dto2.setId(2);

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of(dto1, dto2));
        doReturn(Comparator.comparing(CommentSummaryDto::getId))
                .when(sortingService).comparator(any(), eq(Sort.Direction.DESC));

        var result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertEquals(2, result.size());
        verify(commentRepository).findCommentSummaryUnified(userId, type, true);
        verify(sortingService).comparator(any(), eq(Sort.Direction.DESC));
    }

    @Test
    public void getAllFromUser_ShouldReturnEmptyListIfNoLikedComments() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of());
        doReturn(Comparator.comparing(CommentSummaryDto::getId))
                .when(sortingService).comparator(any(), eq(Sort.Direction.DESC));

        var result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertTrue(result.isEmpty());
    }

    @Test
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
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfCommentNotFound() {
        int commentId = 100;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userCommentService.calculateLikesAndSaves(commentId));

        verify(userCommentRepository, never()).countByCommentIdAndType(anyInt(), any(TypeOfInteraction.class));
    }

    @Test
    public void getAllFromUser_WithType_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of(dto1, dto2));
        doReturn(Comparator.comparing(
                CommentSummaryDto::getUserCommentInteractionCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
                .when(sortingService).comparator(any(), eq(Sort.Direction.DESC));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, ((CommentSummaryDto) result.get(0)).getId());
        assertEquals(1, ((CommentSummaryDto) result.get(1)).getId());
        verify(commentRepository).findCommentSummaryUnified(userId, type, true);
        verify(sortingService).comparator(any(), eq(Sort.Direction.DESC));
    }

    @Test
    public void getAllFromUser_WithType_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.DISLIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of(dto2, dto1));
        doReturn(Comparator.comparing(
                CommentSummaryDto::getUserCommentInteractionCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder())))
                .when(sortingService).comparator(any(), eq(Sort.Direction.ASC));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, ((CommentSummaryDto) result.get(0)).getId());
        assertEquals(2, ((CommentSummaryDto) result.get(1)).getId());
        verify(commentRepository).findCommentSummaryUnified(userId, type, true);
        verify(sortingService).comparator(any(), eq(Sort.Direction.ASC));
    }

    @Test
    public void getAllFromUser_WithType_DefaultShouldSortDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of(dto1, dto2));
        doReturn(Comparator.comparing(
                CommentSummaryDto::getUserCommentInteractionCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
                .when(sortingService).comparator(any(), eq(Sort.Direction.DESC));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, ((CommentSummaryDto) result.get(0)).getId());
        assertEquals(1, ((CommentSummaryDto) result.get(1)).getId());
        verify(commentRepository).findCommentSummaryUnified(userId, type, true);
        verify(sortingService).comparator(any(), eq(Sort.Direction.DESC));
    }

    @Test
    public void getAllFromUser_WithType_ShouldHandleNullDates() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;

        CommentSummaryDto dto1 = createCommentSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        CommentSummaryDto dto2 = createCommentSummaryDto(2, null);
        CommentSummaryDto dto3 = createCommentSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of(dto1, dto2, dto3));
        doReturn(Comparator.comparing(
                CommentSummaryDto::getUserCommentInteractionCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
                .when(sortingService).comparator(any(), eq(Sort.Direction.DESC));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3, ((CommentSummaryDto) result.get(0)).getId());
        assertEquals(1, ((CommentSummaryDto) result.get(1)).getId());
        assertEquals(2, ((CommentSummaryDto) result.get(2)).getId());
        verify(commentRepository).findCommentSummaryUnified(userId, type, true);
        verify(sortingService).comparator(any(), eq(Sort.Direction.DESC));
    }

    @Test
    public void getAllFromUser_WithType_ShouldPopulateAuthorImageUrls() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.LIKE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        dto1.setAuthorImageName("author1.jpg");
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);
        dto2.setAuthorImageName("author2.jpg");

        when(commentRepository.findCommentSummaryUnified(userId, type, true))
                .thenReturn(List.of(dto1, dto2));
        doReturn(Comparator.comparing(CommentSummaryDto::getId))
                .when(sortingService).comparator(any(), eq(Sort.Direction.DESC));

        List<BaseUserEntity> result = userCommentService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(imageUrlPopulationService, times(2)).populateAuthorImage(
                any(CommentSummaryDto.class), any(), any());
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
