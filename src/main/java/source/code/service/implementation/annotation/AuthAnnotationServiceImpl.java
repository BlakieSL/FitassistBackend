package source.code.service.implementation.annotation;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.forum.Comment;
import source.code.model.forum.ForumThread;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Service
public class AuthAnnotationServiceImpl {
    private final CommentRepository commentRepository;
    private final RepositoryHelper repositoryHelper;
    private final ForumThreadRepository forumThreadRepository;
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;

    public AuthAnnotationServiceImpl(CommentRepository commentRepository,
                                     RepositoryHelper repositoryHelper,
                                     ForumThreadRepository forumThreadRepository,
                                     PlanRepository planRepository,
                                     RecipeRepository recipeRepository) {
        this.commentRepository = commentRepository;
        this.repositoryHelper = repositoryHelper;
        this.forumThreadRepository = forumThreadRepository;
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
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
        Integer ownerId = switch (parentType) {
            case COMMENT -> repositoryHelper.find(commentRepository, Comment.class, parentId)
                    .getUser().getId();
            case FORUM_THREAD -> repositoryHelper.find(forumThreadRepository, ForumThread.class, parentId)
                    .getUser().getId();
            case PLAN -> repositoryHelper.find(planRepository, Plan.class, parentId)
                    .getUser().getId();
            case RECIPE -> repositoryHelper.find(recipeRepository, Recipe.class, parentId)
                    .getUser().getId();
            default -> null;
        };

        return AuthorizationUtil.isOwnerOrAdmin(ownerId);
    }
}
