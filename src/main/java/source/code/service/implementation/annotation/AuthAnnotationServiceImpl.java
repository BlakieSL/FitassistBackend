package source.code.service.implementation.annotation;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.Enum.model.TextType;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ThreadComplaint;
import source.code.model.daily.DailyCartActivity;
import source.code.model.media.Media;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.model.text.PlanInstruction;
import source.code.model.text.RecipeInstruction;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetGroup;
import source.code.repository.*;
import source.code.service.declaration.helpers.RepositoryHelper;

@Service
public class AuthAnnotationServiceImpl {
    private final CommentRepository commentRepository;
    private final RepositoryHelper repositoryHelper;
    private final ForumThreadRepository forumThreadRepository;
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final MediaRepository mediaRepository;
    private final RecipeInstructionRepository recipeInstructionRepository;
    private final PlanInstructionRepository planInstructionRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSetGroupRepository workoutSetGroupRepository;
    private final CommentComplaintRepository commentComplaintRepository;
    private final ThreadComplaintRepository threadComplaintRepository;
    private final DailyActivityItemRepository dailyActivityItemRepository;

    public AuthAnnotationServiceImpl(CommentRepository commentRepository,
                                     RepositoryHelper repositoryHelper,
                                     ForumThreadRepository forumThreadRepository,
                                     PlanRepository planRepository,
                                     RecipeRepository recipeRepository,
                                     MediaRepository mediaRepository,
                                     RecipeInstructionRepository recipeInstructionRepository,
                                     PlanInstructionRepository planInstructionRepository,
                                     WorkoutRepository workoutRepository,
                                     WorkoutSetRepository workoutSetRepository, WorkoutSetGroupRepository workoutSetGroupRepository, CommentComplaintRepository commentComplaintRepository, ThreadComplaintRepository threadComplaintRepository, DailyActivityItemRepository dailyActivityItemRepository) {
        this.commentRepository = commentRepository;
        this.repositoryHelper = repositoryHelper;
        this.forumThreadRepository = forumThreadRepository;
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.mediaRepository = mediaRepository;
        this.recipeInstructionRepository = recipeInstructionRepository;
        this.planInstructionRepository = planInstructionRepository;
        this.workoutRepository = workoutRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutSetGroupRepository = workoutSetGroupRepository;
        this.commentComplaintRepository = commentComplaintRepository;
        this.threadComplaintRepository = threadComplaintRepository;
        this.dailyActivityItemRepository = dailyActivityItemRepository;
    }

    public boolean isCommentOwnerOrAdmin(int commentId)  {
        Comment comment = repositoryHelper.find(commentRepository, Comment.class, commentId);
        return AuthorizationUtil.isOwnerOrAdmin(comment.getUser().getId());
    }

    public boolean isForumThreadOwnerOrAdmin(int forumThreadId) {
        ForumThread forumThread = repositoryHelper.find(
                forumThreadRepository,
                ForumThread.class,
                forumThreadId
        );
        return AuthorizationUtil.isOwnerOrAdmin(forumThread.getUser().getId());
    }

    public boolean isPlanOwnerOrAdmin(int planId) {
        Plan plan = repositoryHelper.find(planRepository, Plan.class, planId);
        return AuthorizationUtil.isOwnerOrAdmin(plan.getUser().getId());
    }

    public boolean isRecipeOwnerOrAdmin(int recipeId) {
        Recipe recipe = repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
        return AuthorizationUtil.isOwnerOrAdmin(recipe.getUser().getId());
    }

    public boolean isOwnerOrAdminForParentEntity(MediaConnectedEntity parentType, int parentId) {
        Integer ownerId = findOwnerIdByParentTypeAndId(parentType, parentId);
        return AuthorizationUtil.isOwnerOrAdmin(ownerId);
    }

    public boolean isMediaOwnerOrAdmin(int mediaId) {
        Media media = repositoryHelper.find(mediaRepository, Media.class, mediaId);
        Integer ownerId = findOwnerIdByParentTypeAndId(media.getParentType(), media.getParentId());
        return AuthorizationUtil.isOwnerOrAdmin(ownerId);
    }

    public boolean isTextOwnerOrAdmin(int id, TextType type) {
        Integer ownerId = switch (type) {
            case RECIPE_INSTRUCTION -> repositoryHelper
                    .find(recipeInstructionRepository, RecipeInstruction.class, id)
                    .getRecipe().getUser().getId();
            case PLAN_INSTRUCTION -> repositoryHelper
                    .find(planInstructionRepository, PlanInstruction.class, id)
                    .getPlan().getUser().getId();
            default -> null;
        };
        return AuthorizationUtil.isOwnerOrAdmin(ownerId);
    }

    public boolean isWorkoutOwnerOrAdmin(int workoutId) {
        Workout workout = repositoryHelper.find(workoutRepository, Workout.class, workoutId);
        return AuthorizationUtil.isOwnerOrAdmin(workout.getPlan().getUser().getId());
    }

    public boolean isWorkoutSetGroupOwnerOrAdmin(int workoutSetGroupId) {
        WorkoutSetGroup workoutSetGroup = repositoryHelper.find(
                workoutSetGroupRepository,
                WorkoutSetGroup.class,
                workoutSetGroupId
        );
        return AuthorizationUtil.isOwnerOrAdmin(workoutSetGroup
                .getWorkout()
                .getPlan()
                .getUser()
                .getId());
    }

    public boolean isWorkoutSetOwnerOrAdmin(int workoutSetId) {
        WorkoutSet workoutSet = repositoryHelper.find(
                workoutSetRepository,
                WorkoutSet.class,
                workoutSetId
        );
        return AuthorizationUtil.isOwnerOrAdmin(workoutSet
                .getWorkoutSetGroup()
                .getWorkout()
                .getPlan()
                .getUser()
                .getId());
    }

    public boolean isDailyCartOwner(int dailyActivityItemId) {
        DailyCartActivity dailyCartActivity = repositoryHelper
                .find(dailyActivityItemRepository, DailyCartActivity.class, dailyActivityItemId);

        return AuthorizationUtil.isOwnerOrAdmin(dailyCartActivity.getDailyCart().getUser().getId());
    }

    private Integer findOwnerIdByParentTypeAndId(MediaConnectedEntity parentType, int parentId) {
        return switch (parentType) {
            case COMMENT_COMPLAINT -> repositoryHelper
                    .find(commentComplaintRepository, CommentComplaint.class, parentId)
                    .getUser().getId();
            case THREAD_COMPLAINT -> repositoryHelper
                    .find(threadComplaintRepository, ThreadComplaint.class, parentId)
                    .getUser().getId();
            case COMMENT -> repositoryHelper
                    .find(commentRepository, Comment.class, parentId)
                    .getUser().getId();
            case FORUM_THREAD -> repositoryHelper.
                    find(forumThreadRepository, ForumThread.class, parentId)
                    .getUser().getId();
            case PLAN -> repositoryHelper
                    .find(planRepository, Plan.class, parentId)
                    .getUser().getId();
            case RECIPE -> repositoryHelper
                    .find(recipeRepository, Recipe.class, parentId)
                    .getUser().getId();
            default -> null;
        };
    }
}
