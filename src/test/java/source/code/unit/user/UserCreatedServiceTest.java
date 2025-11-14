package source.code.unit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeRepository;
import source.code.service.implementation.user.UserCreatedServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCreatedServiceTest {
    @Mock
    private PlanRepository planRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ForumThreadRepository forumThreadRepository;

    @Mock
    private PlanMapper planMapper;
    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ForumThreadMapper forumThreadMapper;
    @InjectMocks
    private UserCreatedServiceImpl userCreatedService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("getCreatedPlans - Should return PlanSummaryDto list from repository")
    public void getCreatedPlans_ShouldReturnPlanSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        PlanSummaryDto dto2 = new PlanSummaryDto();

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(planRepository).findSummaryByUserId(true, userId);
    }

    @Test
    @DisplayName("getCreatedPlans - Should return empty list when user has no plans")
    public void getCreatedPlans_ShouldReturnEmptyListWhenNoPlans() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of());

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planRepository).findSummaryByUserId(true, userId);
    }

    @Test
    @DisplayName("getCreatedRecipes - Should return RecipeSummaryDto list from repository")
    public void getCreatedRecipes_ShouldReturnRecipeSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        RecipeSummaryDto dto2 = new RecipeSummaryDto();

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2));

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(recipeRepository).findSummaryByUserId(true, userId);
    }

    @Test
    @DisplayName("getCreatedRecipes - Should return empty list when user has no recipes")
    public void getCreatedRecipes_ShouldReturnEmptyListWhenNoRecipes() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of());

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findSummaryByUserId(true, userId);
    }

    @Test
    @DisplayName("getCreatedComments - Should return CommentSummaryDto list from repository")
    public void getCreatedComments_ShouldReturnCommentSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        CommentSummaryDto dto1 = new CommentSummaryDto();
        CommentSummaryDto dto2 = new CommentSummaryDto();

        when(commentRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto1, dto2));

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(commentRepository).findSummaryByUserId(userId);
    }

    @Test
    @DisplayName("getCreatedComments - Should return empty list when user has no comments")
    public void getCreatedComments_ShouldReturnEmptyListWhenNoComments() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(commentRepository.findSummaryByUserId(userId)).thenReturn(List.of());

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findSummaryByUserId(userId);
    }

    @Test
    @DisplayName("getCreatedThreads - Should return ForumThreadSummaryDto list from repository")
    public void getCreatedThreads_ShouldReturnForumThreadSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
        ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();

        when(forumThreadRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto1, dto2));

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(forumThreadRepository).findSummaryByUserId(userId);
    }

    @Test
    @DisplayName("getCreatedThreads - Should return empty list when user has no threads")
    public void getCreatedThreads_ShouldReturnEmptyListWhenNoThreads() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(forumThreadRepository.findSummaryByUserId(userId)).thenReturn(List.of());

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(forumThreadRepository).findSummaryByUserId(userId);
    }
}