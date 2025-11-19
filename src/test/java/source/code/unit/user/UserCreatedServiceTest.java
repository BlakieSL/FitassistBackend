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
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.media.Media;
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
import source.code.repository.MediaRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeCategoryAssociationRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.user.UserCreatedServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private MediaRepository mediaRepository;
    @Mock
    private AwsS3Service awsS3Service;
    @Mock
    private RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;

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
        dto1.setImageName("image1.jpg");
        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setImageName("image2.jpg");

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2));
        when(awsS3Service.getImage("image1.jpg")).thenReturn("https://s3.amazonaws.com/bucket/image1.jpg");
        when(awsS3Service.getImage("image2.jpg")).thenReturn("https://s3.amazonaws.com/bucket/image2.jpg");

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, "DESC");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        assertEquals("https://s3.amazonaws.com/bucket/image1.jpg", dto1.getFirstImageUrl());
        assertEquals("https://s3.amazonaws.com/bucket/image2.jpg", dto2.getFirstImageUrl());
        verify(planRepository).findSummaryByUserId(true, userId);
        verify(awsS3Service).getImage("image1.jpg");
        verify(awsS3Service).getImage("image2.jpg");
    }

    @Test
    @DisplayName("getCreatedPlans - Should return empty list when user has no plans")
    public void getCreatedPlans_ShouldReturnEmptyListWhenNoPlans() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of());

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, "DESC");

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
        dto1.setId(1);
        dto1.setImageName("recipe1.jpg");
        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);
        dto2.setImageName("recipe2.jpg");

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2));
        when(recipeCategoryAssociationRepository.findCategoryDataByRecipeIds(List.of(1, 2)))
                .thenReturn(Collections.emptyList());
        when(awsS3Service.getImage("recipe1.jpg")).thenReturn("https://s3.amazonaws.com/bucket/recipe1.jpg");
        when(awsS3Service.getImage("recipe2.jpg")).thenReturn("https://s3.amazonaws.com/bucket/recipe2.jpg");

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, "DESC");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        assertEquals("https://s3.amazonaws.com/bucket/recipe1.jpg", dto1.getFirstImageUrl());
        assertEquals("https://s3.amazonaws.com/bucket/recipe2.jpg", dto2.getFirstImageUrl());
        verify(recipeRepository).findSummaryByUserId(true, userId);
        verify(recipeCategoryAssociationRepository).findCategoryDataByRecipeIds(List.of(1, 2));
        verify(awsS3Service).getImage("recipe1.jpg");
        verify(awsS3Service).getImage("recipe2.jpg");
    }

    @Test
    @DisplayName("getCreatedRecipes - Should return empty list when user has no recipes")
    public void getCreatedRecipes_ShouldReturnEmptyListWhenNoRecipes() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of());

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, "DESC");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findSummaryByUserId(true, userId);
    }

    @Test
    @DisplayName("getCreatedComments - Should return CommentSummaryDto list from repository")
    public void getCreatedComments_ShouldReturnCommentSummaryDtos() {
        int userId = 1;

        CommentSummaryDto dto1 = new CommentSummaryDto();
        dto1.setAuthorId(1);
        dto1.setAuthorImageUrl("author1.jpg");
        CommentSummaryDto dto2 = new CommentSummaryDto();
        dto2.setAuthorId(2);
        dto2.setAuthorImageUrl("author2.jpg");

        when(commentRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto1, dto2));
        when(awsS3Service.getImage("author1.jpg")).thenReturn("https://s3.amazonaws.com/bucket/author1.jpg");
        when(awsS3Service.getImage("author2.jpg")).thenReturn("https://s3.amazonaws.com/bucket/author2.jpg");

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, "DESC");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        assertEquals("https://s3.amazonaws.com/bucket/author1.jpg", dto1.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/bucket/author2.jpg", dto2.getAuthorImageUrl());
        verify(commentRepository).findSummaryByUserId(userId);
        verify(awsS3Service).getImage("author1.jpg");
        verify(awsS3Service).getImage("author2.jpg");
    }

    @Test
    @DisplayName("getCreatedComments - Should return empty list when user has no comments")
    public void getCreatedComments_ShouldReturnEmptyListWhenNoComments() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(commentRepository.findSummaryByUserId(userId)).thenReturn(List.of());

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, "DESC");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findSummaryByUserId(userId);
    }

    @Test
    @DisplayName("getCreatedThreads - Should return ForumThreadSummaryDto list from repository")
    public void getCreatedThreads_ShouldReturnForumThreadSummaryDtos() {
        int userId = 1;

        ForumThreadSummaryDto dto1 = new ForumThreadSummaryDto();
        dto1.setAuthorId(1);
        dto1.setAuthorImageUrl("author1.jpg");
        ForumThreadSummaryDto dto2 = new ForumThreadSummaryDto();
        dto2.setAuthorId(2);
        dto2.setAuthorImageUrl("author2.jpg");

        when(forumThreadRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto1, dto2));
        when(awsS3Service.getImage("author1.jpg")).thenReturn("https://s3.amazonaws.com/bucket/author1.jpg");
        when(awsS3Service.getImage("author2.jpg")).thenReturn("https://s3.amazonaws.com/bucket/author2.jpg");

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, "DESC");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        assertEquals("https://s3.amazonaws.com/bucket/author1.jpg", dto1.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/bucket/author2.jpg", dto2.getAuthorImageUrl());
        verify(forumThreadRepository).findSummaryByUserId(userId);
        verify(awsS3Service).getImage("author1.jpg");
        verify(awsS3Service).getImage("author2.jpg");
    }

    @Test
    @DisplayName("getCreatedThreads - Should return empty list when user has no threads")
    public void getCreatedThreads_ShouldReturnEmptyListWhenNoThreads() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(forumThreadRepository.findSummaryByUserId(userId)).thenReturn(List.of());

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, "DESC");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(forumThreadRepository).findSummaryByUserId(userId);
    }

    @Test
    @DisplayName("getCreatedPlans - Should handle plans with null image URLs")
    public void getCreatedPlans_ShouldHandleNullImageUrls() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setImageName(null);

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, "DESC");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(dto1.getFirstImageUrl());
        verify(planRepository).findSummaryByUserId(true, userId);
        verifyNoInteractions(awsS3Service);
    }

    @Test
    @DisplayName("getCreatedRecipes - Should handle recipes with null image URLs")
    public void getCreatedRecipes_ShouldHandleNullImageUrls() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setImageName(null);

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1));
        when(recipeCategoryAssociationRepository.findCategoryDataByRecipeIds(List.of(1)))
                .thenReturn(Collections.emptyList());

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, "DESC");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(dto1.getFirstImageUrl());
        verify(recipeRepository).findSummaryByUserId(true, userId);
        verify(recipeCategoryAssociationRepository).findCategoryDataByRecipeIds(List.of(1));
        verifyNoInteractions(awsS3Service);
    }

    @Test
    @DisplayName("getCreatedPlans with sortDirection - Should sort by createdAt DESC by default")
    public void getCreatedPlans_ShouldSortByCreatedAtDesc() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, "DESC");

        assertSortedResult(result, 2, 2, 1);
    }

    @Test
    @DisplayName("getCreatedPlans with sortDirection - Should sort by createdAt ASC")
    public void getCreatedPlans_ShouldSortByCreatedAtAsc() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, older);
        PlanSummaryDto dto2 = createPlanSummaryDto(2, newer);

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto2, dto1));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, "ASC");

        assertSortedResult(result, 2, 1, 2);
    }

    @Test
    @DisplayName("getCreatedRecipes with sortDirection - Should sort by createdAt DESC by default")
    public void getCreatedRecipes_ShouldSortByCreatedAtDesc() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2));
        when(recipeCategoryAssociationRepository.findCategoryDataByRecipeIds(List.of(1, 2)))
                .thenReturn(Collections.emptyList());

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, "DESC");

        assertSortedResult(result, 2, 2, 1);
    }

    @Test
    @DisplayName("getCreatedRecipes with sortDirection - Should sort by createdAt ASC")
    public void getCreatedRecipes_ShouldSortByCreatedAtAsc() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto2, dto1));
        when(recipeCategoryAssociationRepository.findCategoryDataByRecipeIds(List.of(2, 1)))
                .thenReturn(Collections.emptyList());

        List<RecipeSummaryDto> result = userCreatedService.getCreatedRecipes(userId, "ASC");

        assertSortedResult(result, 2, 1, 2);
    }

    @Test
    @DisplayName("getCreatedComments with sortDirection - Should sort by dateCreated DESC by default")
    public void getCreatedComments_ShouldSortByDateCreatedDesc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto1, dto2));

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, "DESC");

        assertSortedResult(result, 2, 2, 1);
    }

    @Test
    @DisplayName("getCreatedComments with sortDirection - Should sort by dateCreated ASC")
    public void getCreatedComments_ShouldSortByDateCreatedAsc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        CommentSummaryDto dto1 = createCommentSummaryDto(1, older);
        CommentSummaryDto dto2 = createCommentSummaryDto(2, newer);

        when(commentRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto2, dto1));

        List<CommentSummaryDto> result = userCreatedService.getCreatedComments(userId, "ASC");

        assertSortedResult(result, 2, 1, 2);
    }

    @Test
    @DisplayName("getCreatedThreads with sortDirection - Should sort by dateCreated DESC by default")
    public void getCreatedThreads_ShouldSortByDateCreatedDesc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ForumThreadSummaryDto dto1 = createForumThreadSummaryDto(1, older);
        ForumThreadSummaryDto dto2 = createForumThreadSummaryDto(2, newer);

        when(forumThreadRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto1, dto2));

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, "DESC");

        assertSortedResult(result, 2, 2, 1);
    }

    @Test
    @DisplayName("getCreatedThreads with sortDirection - Should sort by dateCreated ASC")
    public void getCreatedThreads_ShouldSortByDateCreatedAsc() {
        int userId = 1;

        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ForumThreadSummaryDto dto1 = createForumThreadSummaryDto(1, older);
        ForumThreadSummaryDto dto2 = createForumThreadSummaryDto(2, newer);

        when(forumThreadRepository.findSummaryByUserId(userId)).thenReturn(List.of(dto2, dto1));

        List<ForumThreadSummaryDto> result = userCreatedService.getCreatedThreads(userId, "ASC");

        assertSortedResult(result, 2, 1, 2);
    }

    @Test
    @DisplayName("sortByCreatedAt - Should handle null dates properly")
    public void sortByCreatedAt_ShouldHandleNullDates() {
        int userId = 1;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        PlanSummaryDto dto1 = createPlanSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        PlanSummaryDto dto2 = createPlanSummaryDto(2, null);
        PlanSummaryDto dto3 = createPlanSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(planRepository.findSummaryByUserId(true, userId)).thenReturn(List.of(dto1, dto2, dto3));

        List<PlanSummaryDto> result = userCreatedService.getCreatedPlans(userId, "DESC");

        assertSortedResult(result, 3, 3, 1, 2);
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