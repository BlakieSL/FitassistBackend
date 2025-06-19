package source.code.unit.user.withType;

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
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.user.UserRecipe;
import source.code.model.user.User;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withType.UserRecipeServiceImpl;

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
    @DisplayName("saveToUser - Should save to user with type")
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int recipeId = 100;
        short type = 1;
        User user = new User();
        Recipe recipe = new Recipe();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        userRecipeService.saveToUser(recipeId, type);

        verify(userRecipeRepository).save(any(UserRecipe.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int recipeId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userRecipeService.saveToUser(recipeId, type));

        verify(userRecipeRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int recipeId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.saveToUser(recipeId, type));

        verify(userRecipeRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if recipe not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfRecipeNotFound() {
        int userId = 1;
        int recipeId = 100;
        short type = 1;
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
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int recipeId = 100;
        short type = 1;
        UserRecipe userRecipe = UserRecipe.createWithUserRecipeType(new User(), new Recipe(), type);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.findByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(Optional.of(userRecipe));

        userRecipeService.deleteFromUser(recipeId, type);

        verify(userRecipeRepository).delete(userRecipe);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if user recipe not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserRecipeNotFound() {
        int userId = 1;
        int recipeId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.findByUserIdAndRecipeIdAndType(userId, recipeId, type))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.deleteFromUser(recipeId, type));

        verify(userRecipeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all recipes by type")
    public void getAllFromUser_ShouldReturnAllRecipesByType() {
        int userId = 1;
        short type = 1;
        UserRecipe recipe1 = UserRecipe.createWithUserRecipeType(new User(), new Recipe(), type);
        UserRecipe recipe2 = UserRecipe.createWithUserRecipeType(new User(), new Recipe(), type);
        RecipeResponseDto dto1 = new RecipeResponseDto();
        RecipeResponseDto dto2 = new RecipeResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of(recipe1, recipe2));
        when(recipeMapper.toResponseDto(recipe1.getRecipe())).thenReturn(dto1);
        when(recipeMapper.toResponseDto(recipe2.getRecipe())).thenReturn(dto2);

        var result = userRecipeService.getAllFromUser(type);

        assertEquals(2, result.size());
        assertTrue(result.contains((BaseUserEntity) dto1));
        assertTrue(result.contains((BaseUserEntity) dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no recipes")
    public void getAllFromUser_ShouldReturnEmptyListIfNoRecipes() {
        int userId = 1;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userRecipeRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userRecipeService.getAllFromUser(type);

        assertTrue(result.isEmpty());
        verify(recipeMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int recipeId = 100;
        long saveCount = 5;
        long likeCount = 10;
        Recipe recipe = new Recipe();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRecipeRepository.countByRecipeIdAndType(recipeId, (short) 1))
                .thenReturn(saveCount);
        when(userRecipeRepository.countByRecipeIdAndType(recipeId, (short) 2))
                .thenReturn(likeCount);

        var result = userRecipeService.calculateLikesAndSaves(recipeId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(recipeRepository).findById(recipeId);
        verify(userRecipeRepository).countByRecipeIdAndType(recipeId, (short) 1);
        verify(userRecipeRepository).countByRecipeIdAndType(recipeId, (short) 2);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if recipe not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfRecipeNotFound() {
        int recipeId = 100;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userRecipeService.calculateLikesAndSaves(recipeId));

        verify(userRecipeRepository, never()).countByRecipeIdAndType(anyInt(), anyShort());
    }
}