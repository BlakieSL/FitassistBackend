package source.code.unit.annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.Enum.model.TextType;
import source.code.helper.utils.AuthorizationUtil;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ThreadComplaint;
import source.code.model.media.Media;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.model.text.PlanInstruction;
import source.code.model.text.RecipeInstruction;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetExercise;
import source.code.repository.*;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.implementation.annotation.AuthAnnotationServiceImpl;

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
	private RecipeInstructionRepository recipeInstructionRepository;

	@Mock
	private PlanInstructionRepository planInstructionRepository;

	@Mock
	private WorkoutRepository workoutRepository;

	@Mock
	private WorkoutSetExerciseRepository workoutSetExerciseRepository;

	@Mock
	private WorkoutSetRepository workoutSetRepository;

	@Mock
	private CommentComplaintRepository commentComplaintRepository;

	@Mock
	private ThreadComplaintRepository threadComplaintRepository;

	@InjectMocks
	private AuthAnnotationServiceImpl authAnnotationService;

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
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isCommentOwnerOrAdmin(commentId);

		assertTrue(result);
	}

	@Test
	void isForumThreadOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int forumThreadId = 1;
		ForumThread forumThread = ForumThread.of(forumThreadId, user);
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId)).thenReturn(forumThread);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isForumThreadOwnerOrAdmin(forumThreadId);

		assertTrue(result);
	}

	@Test
	void isPlanOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int planId = 1;
		Plan plan = Plan.of(planId, user);
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isPlanOwnerOrAdmin(planId);

		assertTrue(result);
	}

	@Test
	void isRecipeOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int recipeId = 1;
		Recipe recipe = Recipe.of(recipeId, user);
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isRecipeOwnerOrAdmin(recipeId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForComment() {
		int commentId = 5;
		Comment comment = Comment.of(commentId, user);
		when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.COMMENT, commentId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForForumThread() {
		int forumThreadId = 6;
		ForumThread forumThread = ForumThread.of(forumThreadId, user);
		when(repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId)).thenReturn(forumThread);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.FORUM_THREAD,
			forumThreadId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForCommentComplaint() {
		int commentComplaintId = 3;
		CommentComplaint commentComplaint = CommentComplaint.of(commentComplaintId, user);
		when(repositoryHelper.find(commentComplaintRepository, CommentComplaint.class, commentComplaintId))
			.thenReturn(commentComplaint);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.COMMENT_COMPLAINT,
			commentComplaintId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForThreadComplaint() {
		int threadComplaintId = 4;
		ThreadComplaint threadComplaint = ThreadComplaint.of(threadComplaintId, user);
		when(repositoryHelper.find(threadComplaintRepository, ThreadComplaint.class, threadComplaintId))
			.thenReturn(threadComplaint);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.THREAD_COMPLAINT,
			threadComplaintId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForPlan() {
		int planId = 7;
		Plan plan = Plan.of(planId, user);
		when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.PLAN, planId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForRecipe() {
		int recipeId = 8;
		Recipe recipe = Recipe.of(recipeId, user);
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.RECIPE, recipeId);

		assertTrue(result);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForFood() {
		int foodId = 10;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(true);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.FOOD, foodId);

		assertTrue(result);
		verifyNoInteractions(repositoryHelper);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForActivity() {
		int activityId = 11;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(false);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.ACTIVITY, activityId);

		assertFalse(result);
		verifyNoInteractions(repositoryHelper);
	}

	@Test
	void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForExercise() {
		int exerciseId = 12;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(false);

		boolean result = authAnnotationService.isOwnerOrAdminForParentEntity(MediaConnectedEntity.EXERCISE, exerciseId);

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
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

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
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(false);

		boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

		assertFalse(result);
	}

	@Test
	void isMediaOwnerOrAdmin_shouldReturnTrueIfAdmin() {
		int mediaId = 8;
		Media media = Media.of(mediaId, MediaConnectedEntity.FOOD, 10);
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(true);

		boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

		assertTrue(result);
	}

	@Test
	void isMediaOwnerOrAdmin_shouldReturnFalseIfNotAdmin() {
		int mediaId = 9;
		Media media = Media.of(mediaId, MediaConnectedEntity.EXERCISE, 11);
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(false);

		boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

		assertFalse(result);
	}

	@Test
	void isTextOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdminForRecipeInstruction() {
		int recipeInstructionId = 7;
		Recipe recipe = Recipe.of(user);
		RecipeInstruction recipeInstruction = RecipeInstruction.of(recipeInstructionId, recipe);
		when(repositoryHelper.find(recipeInstructionRepository, RecipeInstruction.class, recipeInstructionId))
			.thenReturn(recipeInstruction);

		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isTextOwnerOrAdmin(recipeInstructionId, TextType.RECIPE_INSTRUCTION);

		assertTrue(result);
	}

	@Test
	void isTextOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdminForPlanInstruction() {
		int planInstructionId = 8;
		Plan plan = Plan.of(user);
		PlanInstruction planInstruction = PlanInstruction.of(planInstructionId, plan);
		when(repositoryHelper.find(planInstructionRepository, PlanInstruction.class, planInstructionId))
			.thenReturn(planInstruction);

		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isTextOwnerOrAdmin(planInstructionId, TextType.PLAN_INSTRUCTION);

		assertTrue(result);
	}

	@Test
	void isTextOwnerOrAdmin_shouldReturnTrueForAdminWhenOwnerIdIsNullForExerciseInstruction() {
		int exerciseInstructionId = 10;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(true);

		boolean result = authAnnotationService.isTextOwnerOrAdmin(exerciseInstructionId, TextType.EXERCISE_INSTRUCTION);

		assertTrue(result);
	}

	@Test
	void isTextOwnerOrAdmin_shouldReturnFalseIfNotAdminWhenOwnerIdIsNullForExerciseInstruction() {
		int exerciseInstructionId = 11;
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null)).thenReturn(false);

		boolean result = authAnnotationService.isTextOwnerOrAdmin(exerciseInstructionId, TextType.EXERCISE_INSTRUCTION);

		assertFalse(result);
	}

	@Test
	void isWorkoutOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int workoutId = 8;
		Plan plan = Plan.of(user);
		Workout workout = Workout.of(workoutId, plan);
		when(repositoryHelper.find(workoutRepository, Workout.class, workoutId)).thenReturn(workout);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isWorkoutOwnerOrAdmin(workoutId);

		assertTrue(result);
	}

	@Test
	void isWorkoutSetExerciseOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int workoutSetExerciseId = 9;
		Plan plan = Plan.of(user);
		Workout workout = Workout.of(plan);
		WorkoutSet workoutSet = WorkoutSet.of(workout);
		WorkoutSetExercise workoutSetExercise = WorkoutSetExercise.of(workoutSetExerciseId, workoutSet);

		when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId))
			.thenReturn(workoutSetExercise);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isWorkoutSetExerciseOwnerOrAdmin(workoutSetExerciseId);

		assertTrue(result);
	}

	@Test
	void isWorkoutSetOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
		int workoutSetId = 9;
		Plan plan = Plan.of(user);
		Workout workout = Workout.of(plan);
		WorkoutSet workoutSet = WorkoutSet.of(workout);

		when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
		mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId)).thenReturn(true);

		boolean result = authAnnotationService.isWorkoutSetOwnerOrAdmin(workoutSetId);

		assertTrue(result);
	}

}
