package com.fitassist.backend.unit.user.withType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotSupportedInteractionTypeException;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.comment.CommentMapper;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import com.fitassist.backend.model.user.interactions.UserComment;
import com.fitassist.backend.repository.CommentRepository;
import com.fitassist.backend.repository.UserCommentRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.comment.CommentPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withType.UserCommentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCommentServiceTest {

	private static final int USER_ID = 1;

	private static final int COMMENT_ID = 100;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private UserCommentRepository userCommentRepository;

	@Mock
	private CommentMapper commentMapper;

	@Mock
	private CommentPopulationService commentPopulationService;

	private UserCommentServiceImpl userCommentService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	private User user;

	private Comment comment;

	private UserComment userComment;

	@BeforeEach
	void setUp() {
		userCommentService = new UserCommentServiceImpl(userRepository, commentRepository, userCommentRepository,
				commentMapper, commentPopulationService);
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		comment = new Comment();
		comment.setId(COMMENT_ID);
		comment.setUser(user);
		userComment = new UserComment();
		userComment.setUser(user);
		userComment.setComment(comment);
		userComment.setCreatedAt(LocalDateTime.now());
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUser() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		when(userCommentRepository.existsByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

		userCommentService.saveToUser(COMMENT_ID, type);

		verify(userCommentRepository).save(any(UserComment.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotSupportedInteractionTypeExceptionIfTypeIsSave() {
		TypeOfInteraction type = TypeOfInteraction.SAVE;
		when(userCommentRepository.existsByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

		assertThrows(NotSupportedInteractionTypeException.class, () -> userCommentService.saveToUser(COMMENT_ID, type));

		verify(userCommentRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		when(userCommentRepository.existsByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userCommentService.saveToUser(COMMENT_ID, type));

		verify(userCommentRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		when(userCommentRepository.existsByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userCommentService.saveToUser(COMMENT_ID, type));

		verify(userCommentRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfCommentNotFound() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		when(userCommentRepository.existsByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userCommentService.saveToUser(COMMENT_ID, type));

		verify(userCommentRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		when(userCommentRepository.findByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type))
			.thenReturn(Optional.of(userComment));

		userCommentService.deleteFromUser(COMMENT_ID, type);

		verify(userCommentRepository).delete(userComment);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserCommentLikeNotFound() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		when(userCommentRepository.findByUserIdAndCommentIdAndType(USER_ID, COMMENT_ID, type))
			.thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userCommentService.deleteFromUser(COMMENT_ID, type));

		verify(userCommentRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllLikedCommentsFromUser() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Comment comment2 = new Comment();
		comment2.setId(2);
		comment2.setUser(user);
		UserComment uc2 = new UserComment();
		uc2.setComment(comment2);
		uc2.setCreatedAt(LocalDateTime.now());

		CommentSummaryDto dto1 = new CommentSummaryDto();
		dto1.setId(COMMENT_ID);
		CommentSummaryDto dto2 = new CommentSummaryDto();
		dto2.setId(2);

		Page<UserComment> userCommentPage = new PageImpl<>(List.of(userComment, uc2), pageable, 2);
		when(userCommentRepository.findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class)))
			.thenReturn(userCommentPage);
		when(commentMapper.toSummary(comment)).thenReturn(dto1);
		when(commentMapper.toSummary(comment2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userCommentService.getAllFromUser(USER_ID, type, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(userCommentRepository).findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class));
		verify(commentMapper).toSummary(comment);
		verify(commentMapper).toSummary(comment2);
		verify(commentPopulationService).populate(any(List.class));
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoLikedComments() {
		TypeOfInteraction type = TypeOfInteraction.LIKE;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<UserComment> emptyPage = new PageImpl<>(List.of(), pageable, 0);
		when(userCommentRepository.findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class)))
			.thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userCommentService.getAllFromUser(USER_ID, type, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
		verify(userCommentRepository).findAllByUserIdAndType(eq(USER_ID), eq(type), any(Pageable.class));
	}

}
