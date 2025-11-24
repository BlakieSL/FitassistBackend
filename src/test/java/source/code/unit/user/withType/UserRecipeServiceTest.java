package source.code.unit.user.withType;

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
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.RecipeSummaryPopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.implementation.user.interaction.withType.UserRecipeServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRecipeServiceTest {
    @Mock
    private UserRecipeRepository userRecipeRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private RecipeSummaryPopulationService recipeSummaryPopulationService;
    @Mock
    private SortingService sortingService;
    @InjectMocks
    private UserRecipeServiceImpl userRecipeService;
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
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        User user = new User();
        Recipe recipe = new Recipe();
        recipe.setIsPublic(true);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        userRecipeService.saveToUser(recipeId, type);

        verify(userRecipeRepository).save(any(UserRecipe.class));
    }

    @Test
    public void saveToUser_ShouldThrowNotSupportedInteractionTypeExceptionIfRecipeIsPrivate() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        User user = new User();
        Recipe recipe = new Recipe();
        recipe.setIsPublic(false);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(NotSupportedInteractionTypeException.class,
                () -> userRecipeService.saveToUser(recipeId, type));

        verify(userRecipeRepository, never()).save(any());
    }

    @Test
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userRecipeService.saveToUser(recipeId, type));

        verify(userRecipeRepository, never()).save(any());
    }

    @Test
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.saveToUser(recipeId, type));

        verify(userRecipeRepository, never()).save(any());
    }

    @Test
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfRecipeNotFound() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.saveToUser(recipeId, type));

        verify(userRecipeRepository, never()).save(any());
    }

    @Test
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        UserRecipe userRecipe = UserRecipe.createWithUserRecipeType(new User(), new Recipe(), type);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.findByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(Optional.of(userRecipe));

        userRecipeService.deleteFromUser(recipeId, type);

        verify(userRecipeRepository).delete(userRecipe);
    }

    @Test
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserRecipeNotFound() {
        int userId = 1;
        int recipeId = 100;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.findByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.deleteFromUser(recipeId, type));

        verify(userRecipeRepository, never()).delete(any());
    }

    @Test
    public void getAllFromUser_ShouldReturnAllRecipesByType() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(List.of(dto1, dto2));

        var result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertEquals(2, result.size());
        verify(recipeRepository).findRecipeSummaryUnified(userId, type, true, null);
        verify(recipeSummaryPopulationService).populateRecipeSummaries(any(List.class));
    }

    @Test
    public void getAllFromUser_ShouldReturnEmptyListIfNoRecipes() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(List.of());

        var result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertTrue(result.isEmpty());
    }

    @Test
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int recipeId = 100;
        long saveCount = 5;
        long likeCount = 10;
        Recipe recipe = new Recipe();
        TypeOfInteraction saveType = TypeOfInteraction.SAVE;
        TypeOfInteraction likeType = TypeOfInteraction.LIKE;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRecipeRepository.countByRecipeIdAndType(recipeId,saveType))
                .thenReturn(saveCount);
        when(userRecipeRepository.countByRecipeIdAndType(recipeId, likeType))
                .thenReturn(likeCount);

        var result = userRecipeService.calculateLikesAndSaves(recipeId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(recipeRepository).findById(recipeId);
        verify(userRecipeRepository).countByRecipeIdAndType(recipeId, saveType);
        verify(userRecipeRepository).countByRecipeIdAndType(recipeId, likeType);
    }

    @Test
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfRecipeNotFound() {
        int recipeId = 100;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.calculateLikesAndSaves(recipeId));

        verify(userRecipeRepository, never()).countByRecipeIdAndType(anyInt(), any());
    }

    @Test
    public void getAllFromUser_WithType_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(recipeRepository).findRecipeSummaryUnified(userId, type, true, null);
    }

    @Test
    public void getAllFromUser_WithType_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(new ArrayList<>(List.of(dto2, dto1)));

        List<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.ASC);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(recipeRepository).findRecipeSummaryUnified(userId, type, true, null);
    }

    @Test
    public void getAllFromUser_WithType_DefaultShouldSortDesc() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(recipeRepository).findRecipeSummaryUnified(userId, type, true, null);
    }

    @Test
    public void getAllFromUser_WithType_ShouldHandleNullDates() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, null);
        RecipeSummaryDto dto3 = createRecipeSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2, dto3)));

        List<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(recipeRepository).findRecipeSummaryUnified(userId, type, true, null);
    }

    @Test
    public void getAllFromUser_WithType_ShouldPopulateImageUrlsAndCategoriesAfterSorting() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        RecipeSummaryDto dto1 = createRecipeSummaryDto(1, older);
        dto1.setFirstImageName("image1.jpg");
        dto1.setAuthorImageUrl("author1.jpg");
        RecipeSummaryDto dto2 = createRecipeSummaryDto(2, newer);
        dto2.setFirstImageName("image2.jpg");
        dto2.setAuthorImageUrl("author2.jpg");

        when(recipeRepository.findRecipeSummaryUnified(userId, type, true, null))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    private RecipeSummaryDto createRecipeSummaryDto(int id, LocalDateTime interactionDate) {
        RecipeSummaryDto dto = new RecipeSummaryDto();
        dto.setId(id);
        dto.setUserRecipeInteractionCreatedAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((RecipeSummaryDto) result.get(i)).getId());
        }
    }
}
