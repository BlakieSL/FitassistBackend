package source.code.service.implementation.user;

import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.MediaRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final PlanMapper planMapper;
    private final RecipeMapper recipeMapper;
    private final CommentMapper commentMapper;
    private final ForumThreadMapper forumThreadMapper;
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;
    
    public UserCreatedServiceImpl(PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  CommentMapper commentMapper,
                                  ForumThreadMapper forumThreadMapper,
                                  PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  MediaRepository mediaRepository,
                                  AwsS3Service s3Service) {
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.commentMapper = commentMapper;
        this.forumThreadMapper = forumThreadMapper;
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    public List<PlanSummaryDto> getCreatedPlans(int userId) {
        return planRepository.findSummaryByUserId(isOwnProfile(userId), userId);
    }

    @Override
    public List<RecipeSummaryDto> getCreatedRecipes(int userId) {
        return recipeRepository.findSummaryByUserId(isOwnProfile(userId), userId);
    }

    @Override
    public List<CommentSummaryDto> getCreatedComments(int userId) {
        List<CommentSummaryDto> comments = commentRepository.findSummaryByUserId(userId);
        populateAuthorImageUrls(comments);
        return comments;
    }

    @Override
    public List<ForumThreadSummaryDto> getCreatedThreads(int userId) {
        List<ForumThreadSummaryDto> threads = forumThreadRepository.findSummaryByUserId(userId);
        populateAuthorImageUrls(threads);
        return threads;
    }

    private void populateAuthorImageUrls(List<? extends Object> summaryDtos) {
        summaryDtos.forEach(dto -> {
            if (dto instanceof CommentSummaryDto comment) {
                String imageUrl = getAuthorImageUrl(comment.getAuthorId());
                comment.setAuthorImageUrl(imageUrl);
            } else if (dto instanceof ForumThreadSummaryDto thread) {
                String imageUrl = getAuthorImageUrl(thread.getAuthorId());
                thread.setAuthorImageUrl(imageUrl);
            }
        });
    }

    private String getAuthorImageUrl(int userId) {
        return mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(userId, MediaConnectedEntity.USER)
                .map(media -> s3Service.getImage(media.getImageName()))
                .orElse(null);
    }

    private boolean isOwnProfile(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}
