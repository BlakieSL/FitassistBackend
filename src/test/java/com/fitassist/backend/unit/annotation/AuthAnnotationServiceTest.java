package com.fitassist.backend.unit.annotation;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.*;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.implementation.annotation.AuthAnnotationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthAnnotationServiceTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private ForumThreadRepository forumThreadRepository;

	@Mock
	private PlanRepository planRepository;

	@Mock
	private RecipeRepository recipeRepository;

	@Mock
	private MediaRepository mediaRepository;

	@Mock
	private CommentComplaintRepository commentComplaintRepository;

	@Mock
	private ThreadComplaintRepository threadComplaintRepository;

	@InjectMocks
	private AuthAnnotationService authAnnotationService;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	private User user;

	private final int userId = 1;

	@BeforeEach
	void setUp() {
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
		user = new User();
		user.setId(userId);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void isCommentOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int commentId = 1;
		Comment comment = Comment.of(commentId, user);
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isCommentOwnerOrAdminOrModerator(commentId);

		assertTrue(result);
	}

	@Test
	void isForumThreadOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int forumThreadId = 1;
		ForumThread forumThread = ForumThread.of(forumThreadId, user);
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId)).thenReturn(forumThread);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isForumThreadOwnerOrAdminOrModerator(forumThreadId);

		assertTrue(result);
	}

	@Test
	void isPlanOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int planId = 1;
		Plan plan = Plan.of(planId, user);
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isPlanOwnerOrAdminOrModerator(planId);

		assertTrue(result);
	}

	@Test
	void isRecipeOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int recipeId = 1;
		Recipe recipe = Recipe.of(recipeId, user);
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isRecipeOwnerOrAdminOrModerator(recipeId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForComment() {
		int commentId = 5;
		Comment comment = Comment.of(commentId, user);
		when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.COMMENT,
				commentId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForForumThread() {
		int forumThreadId = 6;
		ForumThread forumThread = ForumThread.of(forumThreadId, user);
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId)).thenReturn(forumThread);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService
			.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.FORUM_THREAD, forumThreadId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForCommentComplaint() {
		int commentComplaintId = 3;
		CommentComplaint commentComplaint = CommentComplaint.of(commentComplaintId, user);
		when(repositoryHelper.find(commentComplaintRepository, CommentComplaint.class, commentComplaintId))
			.thenReturn(commentComplaint);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService
			.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.COMMENT_COMPLAINT, commentComplaintId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForThreadComplaint() {
		int threadComplaintId = 4;
		ThreadComplaint threadComplaint = ThreadComplaint.of(threadComplaintId, user);
		when(repositoryHelper.find(threadComplaintRepository, ThreadComplaint.class, threadComplaintId))
			.thenReturn(threadComplaint);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService
			.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.THREAD_COMPLAINT, threadComplaintId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForPlan() {
		int planId = 7;
		Plan plan = Plan.of(planId, user);
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.PLAN,
				planId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForRecipe() {
		int recipeId = 8;
		Recipe recipe = Recipe.of(recipeId, user);
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.RECIPE,
				recipeId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForFood() {
		int foodId = 10;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(null)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.FOOD,
				foodId);

		assertTrue(result);
		verifyNoInteractions(repositoryHelper);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForActivity() {
		int activityId = 11;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(null)).thenReturn(false);

		boolean result = authAnnotationService.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.ACTIVITY,
				activityId);

		assertFalse(result);
		verifyNoInteractions(repositoryHelper);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForExercise() {
		int exerciseId = 12;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(null)).thenReturn(false);

		boolean result = authAnnotationService.isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity.EXERCISE,
				exerciseId);

		assertFalse(result);
		verifyNoInteractions(repositoryHelper);
	}

	@Test
	void isMediaOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdminForPlan() {
		int mediaId = 6;
		int planId = 1;
		Media media = Media.of(mediaId, MediaConnectedEntity.PLAN, planId);
		Plan plan = Plan.of(planId, user);
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		when(repositoryHelper.find(planRepository, Plan.class, media.getParentId())).thenReturn(plan);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(true);

		boolean result = authAnnotationService.isMediaOwnerOrAdminOrModerator(mediaId);

		assertTrue(result);
	}

	@Test
	void isMediaOwnerOrAdmin_shouldReturnFalseIfNotOwnerAndNotAdminForRecipe() {
		int mediaId = 7;
		int recipeId = 2;
		Media media = Media.of(mediaId, MediaConnectedEntity.RECIPE, recipeId);
		Recipe recipe = Recipe.of(recipeId, user);
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		when(repositoryHelper.find(recipeRepository, Recipe.class, media.getParentId())).thenReturn(recipe);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(userId)).thenReturn(false);

		boolean result = authAnnotationService.isMediaOwnerOrAdminOrModerator(mediaId);

		assertFalse(result);
	}

	@Test
	void isMediaOwnerOrAdmin_shouldReturnTrueIfAdmin() {
		int mediaId = 8;
		Media media = Media.of(mediaId, MediaConnectedEntity.FOOD, 10);
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(null)).thenReturn(true);

		boolean result = authAnnotationService.isMediaOwnerOrAdminOrModerator(mediaId);

		assertTrue(result);
	}

	@Test
	void isMediaOwnerOrAdmin_shouldReturnFalseIfNotAdmin() {
		int mediaId = 9;
		Media media = Media.of(mediaId, MediaConnectedEntity.EXERCISE, 11);
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdminOrModerator(null)).thenReturn(false);

		boolean result = authAnnotationService.isMediaOwnerOrAdminOrModerator(mediaId);

		assertFalse(result);
	}

}
