package com.fitassist.backend.service.implementation.annotation;

import com.fitassist.backend.repository.*;
import org.springframework.stereotype.Service;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.daily.DailyCartFood;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import source.code.repository.*;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;

@Service
public class AuthAnnotationServiceImpl {

	private final CommentRepository commentRepository;

	private final RepositoryHelper repositoryHelper;

	private final ForumThreadRepository forumThreadRepository;

	private final PlanRepository planRepository;

	private final RecipeRepository recipeRepository;

	private final MediaRepository mediaRepository;

	private final WorkoutRepository workoutRepository;

	private final WorkoutSetRepository workoutSetRepository;

	private final WorkoutSetExerciseRepository workoutSetExerciseRepository;

	private final CommentComplaintRepository commentComplaintRepository;

	private final ThreadComplaintRepository threadComplaintRepository;

	private final DailyCartActivityRepository dailyCartActivityRepository;

	private final DailyCartFoodRepository dailyCartFoodRepository;

	public AuthAnnotationServiceImpl(CommentRepository commentRepository, RepositoryHelper repositoryHelper,
			ForumThreadRepository forumThreadRepository, PlanRepository planRepository,
			RecipeRepository recipeRepository, MediaRepository mediaRepository, WorkoutRepository workoutRepository,
			WorkoutSetRepository workoutSetRepository, WorkoutSetExerciseRepository workoutSetExerciseRepository,
			CommentComplaintRepository commentComplaintRepository, ThreadComplaintRepository threadComplaintRepository,
			DailyCartActivityRepository dailyCartActivityRepository, DailyCartFoodRepository dailyCartFoodRepository) {
		this.commentRepository = commentRepository;
		this.repositoryHelper = repositoryHelper;
		this.forumThreadRepository = forumThreadRepository;
		this.planRepository = planRepository;
		this.recipeRepository = recipeRepository;
		this.mediaRepository = mediaRepository;
		this.workoutRepository = workoutRepository;
		this.workoutSetRepository = workoutSetRepository;
		this.workoutSetExerciseRepository = workoutSetExerciseRepository;
		this.commentComplaintRepository = commentComplaintRepository;
		this.threadComplaintRepository = threadComplaintRepository;
		this.dailyCartActivityRepository = dailyCartActivityRepository;
		this.dailyCartFoodRepository = dailyCartFoodRepository;
	}

	public boolean isCommentOwnerOrAdminOrModerator(int commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RecordNotFoundException(Comment.class, commentId));
		return AuthorizationUtil.isOwnerOrAdminOrModerator(comment.getUser().getId());
	}

	public boolean isForumThreadOwnerOrAdminOrModerator(int forumThreadId) {
		ForumThread forumThread = repositoryHelper.find(forumThreadRepository, ForumThread.class, forumThreadId);
		return AuthorizationUtil.isOwnerOrAdminOrModerator(forumThread.getUser().getId());
	}

	public boolean isPlanOwnerOrAdminOrModerator(int planId) {
		Plan plan = repositoryHelper.find(planRepository, Plan.class, planId);
		return AuthorizationUtil.isOwnerOrAdminOrModerator(plan.getUser().getId());
	}

	public boolean isPublicPlanOrOwnerOrAdminOrModerator(int planId) {
		Plan plan = repositoryHelper.find(planRepository, Plan.class, planId);
		if (plan.getIsPublic()) {
			return true;
		}
		return AuthorizationUtil.isOwnerOrAdminOrModerator(plan.getUser().getId());
	}

	public boolean isRecipeOwnerOrAdminOrModerator(int recipeId) {
		Recipe recipe = repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
		return AuthorizationUtil.isOwnerOrAdminOrModerator(recipe.getUser().getId());
	}

	public boolean isPublicRecipeOrOwnerOrAdminOrModerator(int recipeId) {
		Recipe recipe = repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
		if (recipe.getIsPublic()) {
			return true;
		}
		return AuthorizationUtil.isOwnerOrAdminOrModerator(recipe.getUser().getId());
	}

	public boolean isOwnerOrAdminOrModeratorForParentEntity(MediaConnectedEntity parentType, int parentId) {
		Integer ownerId = findOwnerIdByParentTypeAndId(parentType, parentId);
		return AuthorizationUtil.isOwnerOrAdminOrModerator(ownerId);
	}

	public boolean isMediaOwnerOrAdminOrModerator(int mediaId) {
		Media media = repositoryHelper.find(mediaRepository, Media.class, mediaId);
		Integer ownerId = findOwnerIdByParentTypeAndId(media.getParentType(), media.getParentId());
		return AuthorizationUtil.isOwnerOrAdminOrModerator(ownerId);
	}

	public boolean isDailyCartOwner(Integer dailyCartActivityId, Integer dailyCartFoodId) {
		Integer userId = null;

		if (dailyCartActivityId != null) {
			DailyCartActivity dailyCartActivity = dailyCartActivityRepository.findByIdWithUser(dailyCartActivityId)
				.orElseThrow(() -> new RecordNotFoundException(DailyCartActivity.class, dailyCartActivityId));
			userId = dailyCartActivity.getDailyCart().getUser().getId();
		}
		else if (dailyCartFoodId != null) {
			DailyCartFood dailyCartFood = dailyCartFoodRepository.findByIdWithUser(dailyCartFoodId)
				.orElseThrow(() -> new RecordNotFoundException(DailyCartFood.class, dailyCartFoodId));
			userId = dailyCartFood.getDailyCart().getUser().getId();
		}

		if (userId == null) {
			return false;
		}

		return AuthorizationUtil.isOwnerOrAdmin(userId);
	}

	private Integer findOwnerIdByParentTypeAndId(MediaConnectedEntity parentType, int parentId) {
		return switch (parentType) {
			case COMMENT_COMPLAINT ->
				repositoryHelper.find(commentComplaintRepository, CommentComplaint.class, parentId).getUser().getId();
			case THREAD_COMPLAINT ->
				repositoryHelper.find(threadComplaintRepository, ThreadComplaint.class, parentId).getUser().getId();
			case COMMENT -> repositoryHelper.find(commentRepository, Comment.class, parentId).getUser().getId();
			case FORUM_THREAD ->
				repositoryHelper.find(forumThreadRepository, ForumThread.class, parentId).getUser().getId();
			case PLAN -> repositoryHelper.find(planRepository, Plan.class, parentId).getUser().getId();
			case RECIPE -> repositoryHelper.find(recipeRepository, Recipe.class, parentId).getUser().getId();
			case USER -> parentId;
			default -> null;
		};
	}

}
