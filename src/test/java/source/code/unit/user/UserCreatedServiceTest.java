package source.code.unit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.repository.*;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.declaration.recipe.RecipePopulationService;
import source.code.service.implementation.user.UserCreatedServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private PlanPopulationService planPopulationService;
    @Mock
    private RecipePopulationService recipePopulationService;
    @Mock
    private ImageUrlPopulationService imagePopulationService;
    @InjectMocks
    private UserCreatedServiceImpl userCreatedService;

    private MockedStatic<AuthorizationUtil> mockedAuthUtil;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
        defaultPageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    public void getCreatedPlans_ShouldReturnPlanSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Plan plan1 = new Plan();
        plan1.setId(1);
        Plan plan2 = new Plan();
        plan2.setId(2);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        dto1.setFirstImageName("image1.jpg");
        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setId(2);
        dto2.setFirstImageName("image2.jpg");

        Page<Plan> planPage = new PageImpl<>(List.of(plan1, plan2), defaultPageable, 2);

        when(planRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(planPage);
        when(planMapper.toSummaryDto(plan1)).thenReturn(dto1);
        when(planMapper.toSummaryDto(plan2)).thenReturn(dto2);

        Page<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, defaultPageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(dto1));
        assertTrue(result.getContent().contains(dto2));
        verify(planRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
        verify(planPopulationService).populate(any(List.class));
    }

    @Test
    public void getCreatedPlans_ShouldReturnEmptyPageWhenNoPlans() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Page<Plan> emptyPage = new PageImpl<>(List.of(), defaultPageable, 0);
        when(planRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(emptyPage);

        Page<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, defaultPageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
    }

    @Test
    public void getCreatedRecipes_ShouldReturnRecipeSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Recipe recipe1 = new Recipe();
        recipe1.setId(1);
        Recipe recipe2 = new Recipe();
        recipe2.setId(2);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setFirstImageName("recipe1.jpg");
        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);
        dto2.setFirstImageName("recipe2.jpg");

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe1, recipe2), defaultPageable, 2);

        when(recipeRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toSummaryDto(recipe1)).thenReturn(dto1);
        when(recipeMapper.toSummaryDto(recipe2)).thenReturn(dto2);

        Page<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, defaultPageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(dto1));
        assertTrue(result.getContent().contains(dto2));
        verify(recipeRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
        verify(recipeMapper).toSummaryDto(recipe1);
        verify(recipeMapper).toSummaryDto(recipe2);
        verify(recipePopulationService).populate(any(List.class));
    }

    @Test
    public void getCreatedRecipes_ShouldReturnEmptyPageWhenNoRecipes() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Page<Recipe> emptyPage = new PageImpl<>(List.of(), defaultPageable, 0);
        when(recipeRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(emptyPage);

        Page<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, defaultPageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
    }

    @Test
    public void getCreatedComments_ShouldReturnCommentSummaryDtos() {
        int userId = 1;

        CommentSummaryDto dto1 = new CommentSummaryDto();
        dto1.setId(1);
        dto1.setAuthorId(1);
        dto1.setAuthorImageName("author1.jpg");
        CommentSummaryDto dto2 = new CommentSummaryDto();
        dto2.setId(2);
        dto2.setAuthorId(2);
        dto2.setAuthorImageName("author2.jpg");

        Page<CommentSummaryDto> commentPage = new PageImpl<>(List.of(dto1, dto2), defaultPageable, 2);

        when(commentRepository.findCommentSummaryUnified(eq(userId), eq(null), eq(false), any(Pageable.class))).thenReturn(commentPage);

        Page<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, defaultPageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(dto1));
        assertTrue(result.getContent().contains(dto2));
        verify(commentRepository).findCommentSummaryUnified(eq(userId), eq(null), eq(false), any(Pageable.class));
    }

    @Test
    public void getCreatedComments_ShouldReturnEmptyPageWhenNoComments() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Page<CommentSummaryDto> emptyPage = new PageImpl<>(List.of(), defaultPageable, 0);
        when(commentRepository.findCommentSummaryUnified(eq(userId), eq(null), eq(false), any(Pageable.class))).thenReturn(emptyPage);

        Page<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, defaultPageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findCommentSummaryUnified(eq(userId), eq(null), eq(false), any(Pageable.class));
    }

    @Test
    public void getCreatedThreads_ShouldReturnForumThreadSummaryDtos() {
        int userId = 1;

        ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
        dto1.setId(1);
        dto1.setAuthorId(1);
        dto1.setAuthorImageName("author1.jpg");
        ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();
        dto2.setId(2);
        dto2.setAuthorId(2);
        dto2.setAuthorImageName("author2.jpg");

        Page<ForumThreadSummaryDto> threadPage = new PageImpl<>(List.of(dto1, dto2), defaultPageable, 2);

        when(forumThreadRepository.findThreadSummaryUnified(eq(userId), eq(false), any(Pageable.class))).thenReturn(threadPage);

        Page<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, defaultPageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(dto1));
        assertTrue(result.getContent().contains(dto2));
        verify(forumThreadRepository).findThreadSummaryUnified(eq(userId), eq(false), any(Pageable.class));
    }

    @Test
    public void getCreatedThreads_ShouldReturnEmptyPageWhenNoThreads() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Page<ForumThreadSummaryDto> emptyPage = new PageImpl<>(List.of(), defaultPageable, 0);
        when(forumThreadRepository.findThreadSummaryUnified(eq(userId), eq(false), any(Pageable.class))).thenReturn(emptyPage);

        Page<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, defaultPageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(forumThreadRepository).findThreadSummaryUnified(eq(userId), eq(false), any(Pageable.class));
    }

    @Test
    public void getCreatedPlans_ShouldHandleNullImageUrls() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Plan plan1 = new Plan();
        plan1.setId(1);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        dto1.setFirstImageName(null);

        Page<Plan> planPage = new PageImpl<>(List.of(plan1), defaultPageable, 1);

        when(planRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(planPage);
        when(planMapper.toSummaryDto(plan1)).thenReturn(dto1);

        Page<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, defaultPageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertNull(dto1.getFirstImageUrl());
        verify(planRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
    }

    @Test
    public void getCreatedRecipes_ShouldHandleNullImageUrls() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Recipe recipe1 = new Recipe();
        recipe1.setId(1);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setFirstImageName(null);

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe1), defaultPageable, 1);

        when(recipeRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toSummaryDto(recipe1)).thenReturn(dto1);

        Page<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, defaultPageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertNull(dto1.getFirstImageUrl());
        verify(recipeRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
        verify(recipeMapper).toSummaryDto(recipe1);
        verify(recipePopulationService).populate(any(List.class));
    }

    @Test
    public void getCreatedPlans_ShouldRespectPagination() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt"));

        Plan plan1 = new Plan();
        plan1.setId(1);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);

        Page<Plan> planPage = new PageImpl<>(List.of(plan1), pageable, 15);

        when(planRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(planPage);
        when(planMapper.toSummaryDto(plan1)).thenReturn(dto1);

        Page<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getTotalElements() > 0);
        verify(planRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
    }

    @Test
    public void getCreatedRecipes_ShouldRespectPagination() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        Recipe recipe1 = new Recipe();
        recipe1.setId(1);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe1), pageable, 25);

        when(recipeRepository.findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toSummaryDto(recipe1)).thenReturn(dto1);

        Page<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getTotalElements() > 0);
        verify(recipeRepository).findCreatedByUserWithDetails(eq(userId), eq(true), any(Pageable.class));
    }
}
