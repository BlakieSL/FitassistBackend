package source.code.service.implementation.user;

import org.springframework.stereotype.Service;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
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

    public UserCreatedServiceImpl(UserRepository userRepository,
                                  PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  CommentMapper commentMapper,
                                  ForumThreadMapper forumThreadMapper) {
        this.userRepository = userRepository;
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.commentMapper = commentMapper;
        this.forumThreadMapper = forumThreadMapper;
    }


    @Override
    public List<PlanResponseDto> getCreatedPlans() {
        return getUser().getPlans().stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeResponseDto> getCreatedRecipes() {
        return getUser().getRecipes().stream()
                .map(recipeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDto> getCreatedComments() {
        return getUser().getWrittenComments().stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ForumThreadResponseDto> getCreatedThreads() {
        return getUser().getCreatedForumThreads().stream()
                .map(forumThreadMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private User getUser() {
        int userId = AuthorizationUtil.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
    }
}
