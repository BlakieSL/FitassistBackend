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
import org.springframework.data.domain.Sort;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.*;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.helpers.RecipeSummaryPopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.implementation.user.UserCreatedServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private RecipeSummaryPopulationService recipeSummaryPopulationService;
    @Mock
    private ImageUrlPopulationService imagePopulationService;
    @Mock
    private SortingService sortingService;
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
    public void getCreatedPlans_ShouldReturnPlanSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setFirstImageName("image1.jpg");
        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setFirstImageName("image2.jpg");

        when(planRepository.findPlanSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1, dto2));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(planRepository).findPlanSummaryUnified(userId, null, false, true);
    }

    @Test
    public void getCreatedPlans_ShouldReturnEmptyListWhenNoPlans() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(planRepository.findPlanSummaryUnified(userId, null, false, true)).thenReturn(List.of());

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planRepository).findPlanSummaryUnified(userId, null, false, true);
    }

    @Test
    public void getCreatedRecipes_ShouldReturnRecipeSummaryDtos() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setFirstImageName("recipe1.jpg");
        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);
        dto2.setFirstImageName("recipe2.jpg");

        when(recipeRepository.findRecipeSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1, dto2));

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(recipeRepository).findRecipeSummaryUnified(userId, null, false, true);
        verify(recipeSummaryPopulationService).populateRecipeSummaries(any(List.class));
    }

    @Test
    public void getCreatedRecipes_ShouldReturnEmptyListWhenNoRecipes() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(recipeRepository.findRecipeSummaryUnified(userId, null, false, true)).thenReturn(List.of());

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findRecipeSummaryUnified(userId, null, false, true);
    }

    @Test
    public void getCreatedComments_ShouldReturnCommentSummaryDtos() {
        int userId = 1;

        CommentSummaryDto dto1 = new CommentSummaryDto();
        dto1.setAuthorId(1);
        dto1.setAuthorImageUrl("author1.jpg");
        CommentSummaryDto dto2 = new CommentSummaryDto();
        dto2.setAuthorId(2);
        dto2.setAuthorImageUrl("author2.jpg");

        when(commentRepository.findCommentSummaryUnified(userId, null, false)).thenReturn(List.of(dto1, dto2));

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(commentRepository).findCommentSummaryUnified(userId, null, false);
    }

    @Test
    public void getCreatedComments_ShouldReturnEmptyListWhenNoComments() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(commentRepository.findCommentSummaryUnified(userId, null, false)).thenReturn(List.of());

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findCommentSummaryUnified(userId, null, false);
    }

    @Test
    public void getCreatedThreads_ShouldReturnForumThreadSummaryDtos() {
        int userId = 1;

        ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
        dto1.setAuthorId(1);
        dto1.setAuthorImageUrl("author1.jpg");
        ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();
        dto2.setAuthorId(2);
        dto2.setAuthorImageUrl("author2.jpg");

        when(forumThreadRepository.findThreadSummaryUnified(userId, false)).thenReturn(List.of(dto1, dto2));

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(forumThreadRepository).findThreadSummaryUnified(userId, false);
    }

    @Test
    public void getCreatedThreads_ShouldReturnEmptyListWhenNoThreads() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(forumThreadRepository.findThreadSummaryUnified(userId, false)).thenReturn(List.of());

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(forumThreadRepository).findThreadSummaryUnified(userId, false);
    }

    @Test
    public void getCreatedPlans_ShouldHandleNullImageUrls() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setFirstImageName(null);

        when(planRepository.findPlanSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(dto1.getFirstImageUrl());
        verify(planRepository).findPlanSummaryUnified(userId, null, false, true);
    }

    @Test
    public void getCreatedRecipes_ShouldHandleNullImageUrls() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setFirstImageName(null);

        when(recipeRepository.findRecipeSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1));

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(dto1.getFirstImageUrl());
        verify(recipeRepository).findRecipeSummaryUnified(userId, null, false, true);
        verify(recipeSummaryPopulationService).populateRecipeSummaries(any(List.class));
    }

    @Test
    public void getCreatedPlans_ShouldReturnPlansAndCallSortingService() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(planRepository.findPlanSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1, dto2));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedPlans_ShouldReturnPlansWithAscDirection() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(planRepository.findPlanSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto2, dto1));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedRecipes_ShouldSortByCreatedAtDesc() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findRecipeSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1, dto2));

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedRecipes_ShouldSortByCreatedAtAsc() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findRecipeSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto2, dto1));

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedComments_ShouldSortByDateCreatedDesc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findCommentSummaryUnified(userId, null, false)).thenReturn(List.of(dto1, dto2));

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedComments_ShouldSortByDateCreatedAsc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findCommentSummaryUnified(userId, null, false)).thenReturn(List.of(dto2, dto1));

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedThreads_ShouldSortByDateCreatedDesc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ForumThreadSummaryDto dto1 = createForumThreadSummaryDto(1, older);
        ForumThreadSummaryDto dto2 = createForumThreadSummaryDto(2, newer);

        when(forumThreadRepository.findThreadSummaryUnified(userId, false)).thenReturn(List.of(dto1, dto2));

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCreatedThreads_ShouldSortByDateCreatedAsc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ForumThreadSummaryDto dto1 = createForumThreadSummaryDto(1, older);
        ForumThreadSummaryDto dto2 = createForumThreadSummaryDto(2, newer);

        when(forumThreadRepository.findThreadSummaryUnified(userId, false)).thenReturn(List.of(dto2, dto1));

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void sortByCreatedAt_ShouldHandleNullDates() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        PlanSummaryDto dto2 = createPlanSummaryDto(2, null);
        PlanSummaryDto dto3 = createPlanSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(planRepository.findPlanSummaryUnified(userId, null, false, true)).thenReturn(List.of(dto1, dto2, dto3));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    private PlanSummaryDto createPlanSummaryDto(int id, LocalDateTime createdAt) {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setId(id);
        dto.setCreatedAt(createdAt);
        return dto;
    }

    private RecipeSummaryDto createRecipeSummaryDto(int id, LocalDateTime createdAt) {
        RecipeSummaryDto dto = new RecipeSummaryDto();
        dto.setId(id);
        dto.setCreatedAt(createdAt);
        return dto;
    }

    private CommentSummaryDto createCommentSummaryDto(int id, LocalDateTime dateCreated) {
        CommentSummaryDto dto = new CommentSummaryDto();
        dto.setId(id);
        dto.setDateCreated(dateCreated);
        return dto;
    }

    private ForumThreadSummaryDto createForumThreadSummaryDto(int id, LocalDateTime dateCreated) {
        ForumThreadSummaryDto dto = new ForumThreadSummaryDto();
        dto.setId(id);
        dto.setDateCreated(dateCreated);
        return dto;
    }

    private void assertSortedResult(List<?> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            if (result.get(i) instanceof PlanSummaryDto) {
                assertEquals(expectedIds[i], ((PlanSummaryDto) result.get(i)).getId());
            } else if (result.get(i) instanceof RecipeSummaryDto) {
                assertEquals(expectedIds[i], ((RecipeSummaryDto) result.get(i)).getId());
            } else if (result.get(i) instanceof CommentSummaryDto) {
                assertEquals(expectedIds[i], ((CommentSummaryDto) result.get(i)).getId());
            } else if (result.get(i) instanceof ForumThreadSummaryDto) {
                assertEquals(expectedIds[i], ((ForumThreadSummaryDto) result.get(i)).getId());
            }
        }
    }
}
