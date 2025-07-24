package source.code.service.implementation.user;

import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.user.User;
import source.code.repository.*;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final UserRepository userRepository;
    private final PlanMapper planMapper;
    private final RecipeMapper recipeMapper;
    private final CommentMapper commentMapper;
    private final ForumThreadMapper forumThreadMapper;
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final UserCommentRepository userCommentRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;

    public UserCreatedServiceImpl(UserRepository userRepository,
                                  PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  CommentMapper commentMapper,
                                  ForumThreadMapper forumThreadMapper, PlanRepository planRepository, RecipeRepository recipeRepository, UserCommentRepository userCommentRepository, CommentRepository commentRepository, ForumThreadRepository forumThreadRepository) {
        this.userRepository = userRepository;
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.commentMapper = commentMapper;
        this.forumThreadMapper = forumThreadMapper;
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.userCommentRepository = userCommentRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
    }


    @Override
    public List<PlanResponseDto> getCreatedPlans() {
        return planRepository.findAllByUser_Id(getUserId()).stream()
                .map(planMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<RecipeResponseDto> getCreatedRecipes() {
        return  recipeRepository.findAllByUser_Id(getUserId()).stream()
                .map(recipeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDto> getCreatedComments() {
        return commentRepository.findAllByUser_Id(getUserId()).stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ForumThreadResponseDto> getCreatedThreads() {
        return forumThreadRepository.findAllByUser_Id(getUserId()).stream()
                .map(forumThreadMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}
