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
import org.springframework.data.domain.*;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.recipe.RecipePopulationService;
import source.code.service.implementation.user.interaction.withType.UserRecipeServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private RecipePopulationService recipePopulationService;
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
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Recipe recipe1 = new Recipe();
        recipe1.setId(1);
        Recipe recipe2 = new Recipe();
        recipe2.setId(2);

        User user = new User();
        user.setId(userId);

        UserRecipe userRecipe1 = UserRecipe.createWithUserRecipeType(user, recipe1, type);
        UserRecipe userRecipe2 = UserRecipe.createWithUserRecipeType(user, recipe2, type);

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);

        Page<UserRecipe> userRecipePage = new PageImpl<>(List.of(userRecipe1, userRecipe2), pageable, 2);

        when(userRecipeRepository.findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class)))
                .thenReturn(userRecipePage);
        when(recipeRepository.findByIdsWithDetails(any())).thenReturn(List.of(recipe1, recipe2));
        when(recipeMapper.toSummaryDto(recipe1)).thenReturn(dto1);
        when(recipeMapper.toSummaryDto(recipe2)).thenReturn(dto2);

        Page<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(userRecipeRepository).findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class));
        verify(recipePopulationService).populate(any(List.class));
    }

    @Test
    public void getAllFromUser_ShouldReturnEmptyListIfNoRecipes() {
        int userId = 1;
        TypeOfInteraction type = TypeOfInteraction.SAVE;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserRecipe> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRecipeRepository.findAllByUserIdAndType(eq(userId), eq(type), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<BaseUserEntity> result = userRecipeService.getAllFromUser(userId, type, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

}
