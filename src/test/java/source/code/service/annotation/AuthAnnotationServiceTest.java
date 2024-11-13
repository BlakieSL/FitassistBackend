package source.code.service.annotation;

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
import source.code.helper.user.AuthorizationUtil;
import source.code.model.forum.Comment;
import source.code.model.forum.ForumThread;
import source.code.model.media.Media;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.model.text.PlanInstruction;
import source.code.model.text.RecipeInstruction;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.model.user.User;
import source.code.repository.*;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.implementation.annotation.AuthAnnotationServiceImpl;

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
    private RecipeInstructionRepository recipeInstructionRepository;
    @Mock
    private PlanInstructionRepository planInstructionRepository;
    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private WorkoutSetRepository workoutSetRepository;
    @InjectMocks
    private AuthAnnotationServiceImpl authAnnotationService;

    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;
    private User user;
    private int userId = 1;

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
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setId(commentId);
        when(repositoryHelper.find(commentRepository, Comment.class, commentId))
                .thenReturn(comment);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isCommentOwnerOrAdmin(commentId);

        assertTrue(result);
    }

    @Test
    void isForumThreadOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
        int forumThreadId = 1;
        ForumThread forumThread = new ForumThread();
        forumThread.setUser(user);
        forumThread.setId(forumThreadId);
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId))
                .thenReturn(forumThread);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isForumThreadOwnerOrAdmin(forumThreadId);

        assertTrue(result);
    }

    @Test
    void isPlanOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
        int planId = 1;
        Plan plan = new Plan();
        plan.setUser(user);
        plan.setId(planId);
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isPlanOwnerOrAdmin(planId);

        assertTrue(result);
    }

    @Test
    void isRecipeOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
        int recipeId = 1;
        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.setId(recipeId);
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isRecipeOwnerOrAdmin(recipeId);

        assertTrue(result);
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForComment() {
        int commentId = 5;
        Comment comment = new Comment();
        comment.setUser(user);
        when(repositoryHelper.find(commentRepository, Comment.class, commentId))
                .thenReturn(comment);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.COMMENT, commentId);

        assertTrue(result);
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForForumThread() {
        int forumThreadId = 6;
        ForumThread forumThread = new ForumThread();
        forumThread.setUser(user);
        when(repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId))
                .thenReturn(forumThread);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.FORUM_THREAD, forumThreadId);

        assertTrue(result);
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForPlan() {
        int planId = 7;
        Plan plan = new Plan();
        plan.setUser(user);
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.PLAN, planId);

        assertTrue(result);
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldReturnTrueIfOwnerOrAdminForRecipe() {
        int recipeId = 8;
        Recipe recipe = new Recipe();
        recipe.setUser(user);
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.RECIPE, recipeId);

        assertTrue(result);
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForFood() {
        int foodId = 10;
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(true);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.FOOD, foodId);

        assertTrue(result);
        verify(repositoryHelper, never()).find(any(), any(), anyInt());
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForActivity() {
        int activityId = 11;

        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(false);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.ACTIVITY, activityId);

        assertFalse(result);
        verify(repositoryHelper, never()).find(any(), any(), anyInt());
    }

    @Test
    void isOwnerOrAdminForParentEntity_shouldCheckAdminOnlyWhenOwnerIsNullForExercise() {
        int exerciseId = 12;

        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(false);

        boolean result = authAnnotationService
                .isOwnerOrAdminForParentEntity(MediaConnectedEntity.EXERCISE, exerciseId);

        assertFalse(result);
        verify(repositoryHelper, never()).find(any(), any(), anyInt());
    }

    @Test
    void isMediaOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdminForPlan() {
        int mediaId = 6;
        int planId = 1;
        Media media = new Media();
        media.setParentType(MediaConnectedEntity.PLAN);
        media.setParentId(planId);

        Plan plan = new Plan();
        plan.setId(planId);
        plan.setUser(user);

        when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
        when(repositoryHelper.find(planRepository, Plan.class, media.getParentId()))
                .thenReturn(plan);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

        assertTrue(result);
    }

    @Test
    void isMediaOwnerOrAdmin_shouldReturnFalseIfNotOwnerAndNotAdminForRecipe() {
        int mediaId = 7;
        int recipeId = 2;
        Media media = new Media();
        media.setParentType(MediaConnectedEntity.RECIPE);
        media.setParentId(recipeId);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setUser(user);

        when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
        when(repositoryHelper.find(recipeRepository, Recipe.class, media.getParentId()))
                .thenReturn(recipe);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(false);

        boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

        assertFalse(result);
    }

    @Test
    void isMediaOwnerOrAdmin_shouldReturnTrueIfAdmin() {
        int mediaId = 8;
        Media media = new Media();
        media.setParentType(MediaConnectedEntity.FOOD);
        media.setParentId(10);

        when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(true);

        boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

        assertTrue(result);
    }

    @Test
    void isMediaOwnerOrAdmin_shouldReturnFalseIfNotAdmin() {
        int mediaId = 9;
        Media media = new Media();
        media.setId(mediaId);
        media.setParentType(MediaConnectedEntity.EXERCISE);
        media.setParentId(11);

        when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(false);

        boolean result = authAnnotationService.isMediaOwnerOrAdmin(mediaId);

        assertFalse(result);
    }

    @Test
    void isTextOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdminForRecipeInstruction() {
        int recipeInstructionId = 7;
        RecipeInstruction recipeInstruction = new RecipeInstruction();
        recipeInstruction.setId(recipeInstructionId);

        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipeInstruction.setRecipe(recipe);

        when(repositoryHelper.find(
                recipeInstructionRepository,
                RecipeInstruction.class,
                recipeInstructionId)
        ).thenReturn(recipeInstruction);

        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isTextOwnerOrAdmin(
                recipeInstructionId,
                TextType.RECIPE_INSTRUCTION
        );

        assertTrue(result);
    }

    @Test
    void isTextOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdminForPlanInstruction() {
        int planInstructionId = 8;
        PlanInstruction planInstruction = new PlanInstruction();
        planInstruction.setId(planInstructionId);

        Plan plan = new Plan();
        plan.setUser(user);
        planInstruction.setPlan(plan);

        when(repositoryHelper.find(
                planInstructionRepository,
                PlanInstruction.class,
                planInstructionId)
        ).thenReturn(planInstruction);

        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isTextOwnerOrAdmin(
                planInstructionId,
                TextType.PLAN_INSTRUCTION
        );

        assertTrue(result);
    }


    @Test
    void isTextOwnerOrAdmin_shouldReturnTrueForAdminWhenOwnerIdIsNullForExerciseInstruction() {
        int exerciseInstructionId = 10;

        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(true);

        boolean result = authAnnotationService
                .isTextOwnerOrAdmin(exerciseInstructionId, TextType.EXERCISE_INSTRUCTION);

        assertTrue(result);
    }

    @Test
    void isTextOwnerOrAdmin_shouldReturnFalseIfNotAdminWhenOwnerIdIsNullForExerciseInstruction() {
        int exerciseInstructionId = 11;

        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(null))
                .thenReturn(false);

        boolean result = authAnnotationService
                .isTextOwnerOrAdmin(exerciseInstructionId, TextType.EXERCISE_INSTRUCTION);

        assertFalse(result);
    }


    @Test
    void isWorkoutOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
        int workoutId = 8;
        Workout workout = new Workout();
        workout.setId(8);

        Plan plan = new Plan();
        plan.setUser(user);
        workout.setPlan(plan);

        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId)).thenReturn(workout);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isWorkoutOwnerOrAdmin(workoutId);

        assertTrue(result);
    }

    @Test
    void isWorkoutSetOwnerOrAdmin_shouldReturnTrueIfOwnerOrAdmin() {
        int workoutSetId = 9;
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setId(workoutSetId);

        Workout workout = new Workout();
        Plan plan = new Plan();

        plan.setUser(user);
        workout.setPlan(plan);
        workoutSet.setWorkout(workout);

        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId))
                .thenReturn(workoutSet);
        mockedAuthorizationUtil.when(() -> AuthorizationUtil.isOwnerOrAdmin(userId))
                .thenReturn(true);

        boolean result = authAnnotationService.isWorkoutSetOwnerOrAdmin(workoutSetId);

        assertTrue(result);
    }
}
