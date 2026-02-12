package com.fitassist.backend.unit.comment;

import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.repository.CommentRepository;
import com.fitassist.backend.service.declaration.comment.CommentPopulationService;
import com.fitassist.backend.service.implementation.comment.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceGetRepliesTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private CommentPopulationService commentPopulationService;

	@InjectMocks
	private CommentServiceImpl commentService;

	private Timestamp timestamp;

	@BeforeEach
	void setUp() {
		timestamp = Timestamp.valueOf(LocalDateTime.now());
	}

	@Test
	void getReplies_shouldReturnEmptyListWhenNoRepliesExist() {
		when(commentRepository.findCommentHierarchy(1)).thenReturn(List.of());

		List<CommentResponseDto> result = commentService.getReplies(1);

		assertTrue(result.isEmpty());
		verify(commentRepository).findCommentHierarchy(1);
	}

	@Test
	void getReplies_shouldReturnDirectRepliesWithHierarchy() {
		when(commentRepository.findCommentHierarchy(1))
			.thenReturn(List.of(new Object[] { 2, "Reply 1", 10, 100, 1, timestamp, "user1" },
					new Object[] { 3, "Reply 2", 10, 101, 1, timestamp, "user2" },
					new Object[] { 4, "Nested reply", 10, 102, 2, timestamp, "user3" }));

		List<CommentResponseDto> result = commentService.getReplies(1);

		assertEquals(2, result.size());
		assertEquals(1, result.getFirst().getReplies().size());
		assertEquals(4, result.getFirst().getReplies().getFirst().getId());
		assertEquals(0, result.get(1).getReplies().size());
	}

	@Test
	void getReplies_shouldReturnRepliesInRepositoryOrder() {
		when(commentRepository.findCommentHierarchy(1))
			.thenReturn(List.of(new Object[] { 2, "Reply 1", 10, 100, 1, timestamp, "user1" },
					new Object[] { 3, "Reply 2", 10, 101, 1, timestamp, "user2" },
					new Object[] { 4, "Nested reply", 10, 102, 2, timestamp, "user3" }));

		List<CommentResponseDto> result = commentService.getReplies(1);

		assertEquals(2, result.size());
		assertEquals(2, result.getFirst().getId());
		assertEquals(3, result.get(1).getId());
	}

	@Test
	void getReplies_shouldFilterOutInvalidParentReferences() {
		when(commentRepository.findCommentHierarchy(1))
			.thenReturn(List.of(new Object[] { 2, "Reply 1", 10, 100, 1, timestamp, "user1" },
					new Object[] { 3, "Orphan reply", 10, 101, 999, timestamp, "user2" }));

		List<CommentResponseDto> result = commentService.getReplies(1);

		assertEquals(1, result.size());
		assertEquals(2, result.getFirst().getId());
	}

	@Test
	void getReplies_shouldHandleDeeplyNestedHierarchies() {
		when(commentRepository.findCommentHierarchy(1))
			.thenReturn(List.of(new Object[] { 2, "Level 1", 10, 100, 1, timestamp, "user1" },
					new Object[] { 3, "Level 2", 10, 101, 2, timestamp, "user2" },
					new Object[] { 4, "Level 3", 10, 102, 3, timestamp, "user3" },
					new Object[] { 5, "Level 4", 10, 103, 4, timestamp, "user4" }));

		List<CommentResponseDto> result = commentService.getReplies(1);

		assertEquals(1, result.size());
		assertEquals(2, result.getFirst().getId());
		assertEquals(3, result.getFirst().getReplies().getFirst().getId());
		assertEquals(4, result.getFirst().getReplies().getFirst().getReplies().getFirst().getId());
		assertEquals(5,
				result.getFirst().getReplies().getFirst().getReplies().getFirst().getReplies().getFirst().getId());
	}

	@Test
	void getReplies_shouldMaintainMultipleRootLevelReplies() {
		when(commentRepository.findCommentHierarchy(1))
			.thenReturn(List.of(new Object[] { 2, "Reply A", 10, 100, 1, timestamp, "user1" },
					new Object[] { 3, "Reply B", 10, 101, 1, timestamp, "user2" },
					new Object[] { 4, "Reply C", 10, 102, 1, timestamp, "user3" },
					new Object[] { 5, "Nested under A", 10, 103, 2, timestamp, "user4" }));

		List<CommentResponseDto> result = commentService.getReplies(1);

		assertEquals(3, result.size());
		assertEquals(1, result.getFirst().getReplies().size());
		assertEquals(5, result.getFirst().getReplies().getFirst().getId());
	}

}
