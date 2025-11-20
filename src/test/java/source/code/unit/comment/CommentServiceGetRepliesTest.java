package source.code.unit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.repository.CommentRepository;
import source.code.service.implementation.comment.CommentServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceGetRepliesTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getReplies_shouldReturnEmptyListWhenNoRepliesExist() {
        when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of());

        List<CommentResponseDto> result = commentService.getReplies(1);

        assertTrue(result.isEmpty());
        verify(commentRepository).findCommentHierarchy(1);
    }

    @Test
    void getReplies_shouldReturnDirectRepliesWithHierarchy() {
        when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of(
                new Object[]{2, "Reply 1", 10, 100, 1},
                new Object[]{3, "Reply 2", 10, 101, 1},
                new Object[]{4, "Nested reply", 10, 102, 2}
        ));

        List<CommentResponseDto> result = commentService.getReplies(1);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getReplies().size());
        assertEquals(4, result.get(0).getReplies().get(0).getId());
        assertEquals(0, result.get(1).getReplies().size());
    }

    @Test
    void getReplies_shouldSortRepliesById() {
        when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of(
                new Object[]{3, "Reply 2", 10, 101, 1},
                new Object[]{2, "Reply 1", 10, 100, 1},
                new Object[]{4, "Nested reply", 10, 102, 2}
        ));

        List<CommentResponseDto> result = commentService.getReplies(1);

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getId());
        assertEquals(3, result.get(1).getId());
    }

    @Test
    void getReplies_shouldFilterOutInvalidParentReferences() {
        when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of(
                new Object[]{2, "Reply 1", 10, 100, 1},
                new Object[]{3, "Orphan reply", 10, 101, 999}
        ));

        List<CommentResponseDto> result = commentService.getReplies(1);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
    }

    @Test
    void getReplies_shouldHandleDeeplyNestedHierarchies() {
        when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of(
                new Object[]{2, "Level 1", 10, 100, 1},
                new Object[]{3, "Level 2", 10, 101, 2},
                new Object[]{4, "Level 3", 10, 102, 3},
                new Object[]{5, "Level 4", 10, 103, 4}
        ));

        List<CommentResponseDto> result = commentService.getReplies(1);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
        assertEquals(3, result.get(0).getReplies().get(0).getId());
        assertEquals(4, result.get(0).getReplies().get(0).getReplies().get(0).getId());
        assertEquals(5, result.get(0).getReplies().get(0).getReplies().get(0).getReplies().get(0).getId());
    }

    @Test
    void getReplies_shouldMaintainMultipleRootLevelReplies() {
        when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of(
                new Object[]{2, "Reply A", 10, 100, 1},
                new Object[]{3, "Reply B", 10, 101, 1},
                new Object[]{4, "Reply C", 10, 102, 1},
                new Object[]{5, "Nested under A", 10, 103, 2}
        ));

        List<CommentResponseDto> result = commentService.getReplies(1);

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getReplies().size());
        assertEquals(5, result.get(0).getReplies().get(0).getId());
    }
}
