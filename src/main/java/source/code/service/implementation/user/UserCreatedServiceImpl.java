package source.code.service.implementation.user;

import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;
import java.util.stream.Collectors;

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
    public UserCreatedServiceImpl(PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  CommentMapper commentMapper,
                                  ForumThreadMapper forumThreadMapper,
                                  PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository) {
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.commentMapper = commentMapper;
        this.forumThreadMapper = forumThreadMapper;
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
    }


    @Override
    public List<PlanResponseDto> getCreatedPlans(int userId) {
        return planRepository.findAllByUser_Id(isCurrentUser(userId), userId).stream()
                .map(planMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<RecipeResponseDto> getCreatedRecipes(int userId) {
        return  recipeRepository.findAllByUser_Id(isCurrentUser(userId), getUserId()).stream()
                .map(recipeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDto> getCreatedComments(int userId) {
        return commentRepository.findAllByUser_Id(userId).stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ForumThreadResponseDto> getCreatedThreads(int userId) {
        return forumThreadRepository.findAllByUser_Id(userId).stream()
                .map(forumThreadMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private boolean isCurrentUser(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}
