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
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
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
    @DisplayName("getCreatedPlans - Should return mapped PlanResponseDto list")
    public void getCreatedPlans_ShouldReturnMappedPlanResponseDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();
        Plan plan1 = new Plan();
        Plan plan2 = new Plan();
        user.getPlans().add(plan1);
        user.getPlans().add(plan2);
        PlanResponseDto dto1 = new PlanResponseDto();
        PlanResponseDto dto2 = new PlanResponseDto();

        when(planRepository.findAllByUser_Id(anyBoolean(), anyInt())).thenReturn(List.of(plan1, plan2));

        when(planMapper.toResponseDto(plan1)).thenReturn(dto1);
        when(planMapper.toResponseDto(plan2)).thenReturn(dto2);

        List<PlanResponseDto> result = userCreatedService.getCreatedPlans(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(planRepository).findAllByUser_Id(anyBoolean(), anyInt());
        verify(planMapper, times(2)).toResponseDto(any(Plan.class));
    }

    @Test
    @DisplayName("getCreatedPlans - Should return empty list when user has no plans")
    public void getCreatedPlans_ShouldReturnEmptyListWhenNoPlans() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();

        when(planRepository.findAllByUser_Id(anyBoolean(), anyInt())).thenReturn(List.of());

        List<PlanResponseDto> result = userCreatedService.getCreatedPlans(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planRepository).findAllByUser_Id(anyBoolean(), anyInt());
        verifyNoInteractions(planMapper);
    }

    @Test
    @DisplayName("getCreatedRecipes - Should return mapped RecipeResponseDto list")
    public void getCreatedRecipes_ShouldReturnMappedRecipeResponseDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();
        user.getRecipes().add(recipe1);
        user.getRecipes().add(recipe2);

        when(recipeRepository.findAllByUser_Id(anyBoolean(), anyInt())).thenReturn(List.of(recipe1, recipe2));

        RecipeResponseDto dto1 = new RecipeResponseDto();
        RecipeResponseDto dto2 = new RecipeResponseDto();
        when(recipeMapper.toResponseDto(recipe1)).thenReturn(dto1);
        when(recipeMapper.toResponseDto(recipe2)).thenReturn(dto2);

        List<RecipeResponseDto> result = userCreatedService.getCreatedRecipes(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(recipeRepository).findAllByUser_Id(anyBoolean(), anyInt());
        verify(recipeMapper, times(2)).toResponseDto(any(Recipe.class));
    }

    @Test
    @DisplayName("getCreatedRecipes - Should return empty list when user has no recipes")
    public void getCreatedRecipes_ShouldReturnEmptyListWhenNoRecipes() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();

        when(recipeRepository.findAllByUser_Id(anyBoolean(), anyInt())).thenReturn(List.of());

        List<RecipeResponseDto> result = userCreatedService.getCreatedRecipes(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findAllByUser_Id(anyBoolean(), anyInt());
        verifyNoInteractions(recipeMapper);
    }


    @Test
    @DisplayName("getCreatedComments - Should return mapped CommentResponseDto list")
    public void getCreatedComments_ShouldReturnMappedCommentResponseDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();
        CommentResponseDto dto1 = new CommentResponseDto();
        CommentResponseDto dto2 = new CommentResponseDto();
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        user.getWrittenComments().add(comment1);
        user.getWrittenComments().add(comment2);

        when(commentRepository.findAllByUser_Id(userId)).thenReturn(List.of(comment1, comment2));

        when(commentMapper.toResponseDto(comment1)).thenReturn(dto1);
        when(commentMapper.toResponseDto(comment2)).thenReturn(dto2);

        List<CommentResponseDto> result = userCreatedService.getCreatedComments(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(commentRepository).findAllByUser_Id(userId);
        verify(commentMapper, times(2)).toResponseDto(any(Comment.class));
    }

    @Test
    @DisplayName("getCreatedComments - Should return empty list when user has no comments")
    public void getCreatedComments_ShouldReturnEmptyListWhenNoComments() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();
        when(commentRepository.findAllByUser_Id(userId)).thenReturn(List.of());

        List<CommentResponseDto> result = userCreatedService.getCreatedComments(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findAllByUser_Id(userId);
        verifyNoInteractions(commentMapper);
    }


    @Test
    @DisplayName("getCreatedThreads - Should return mapped ForumThreadResponseDto list")
    public void getCreatedThreads_ShouldReturnMappedForumThreadResponseDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();
        ForumThreadResponseDto dto1 = new ForumThreadResponseDto();
        ForumThreadResponseDto dto2 = new ForumThreadResponseDto();
        ForumThread thread1 = new ForumThread();
        ForumThread thread2 = new ForumThread();
        user.getCreatedForumThreads().add(thread1);
        user.getCreatedForumThreads().add(thread2);

        when(forumThreadRepository.findAllByUser_Id(userId)).thenReturn(List.of(thread1, thread2));
        when(forumThreadMapper.toResponseDto(thread1)).thenReturn(dto1);
        when(forumThreadMapper.toResponseDto(thread2)).thenReturn(dto2);

        List<ForumThreadResponseDto> result = userCreatedService.getCreatedThreads(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(forumThreadRepository).findAllByUser_Id(userId);
        verify(forumThreadMapper, times(2)).toResponseDto(any(ForumThread.class));
    }

    @Test
    @DisplayName("getCreatedThreads - Should return empty list when user has no threads")
    public void getCreatedThreads_ShouldReturnEmptyListWhenNoThreads() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        User user = new User();
        when(forumThreadRepository.findAllByUser_Id(userId)).thenReturn(List.of());

        List<ForumThreadResponseDto> result = userCreatedService.getCreatedThreads(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(forumThreadRepository).findAllByUser_Id(userId);
        verifyNoInteractions(forumThreadMapper);
    }
}