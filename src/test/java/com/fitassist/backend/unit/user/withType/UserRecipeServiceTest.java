package com.fitassist.backend.unit.user.withType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotSupportedInteractionTypeException;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.recipe.RecipeMapper;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import com.fitassist.backend.model.user.interactions.UserRecipe;
import com.fitassist.backend.repository.RecipeRepository;
import com.fitassist.backend.repository.UserRecipeRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.recipe.RecipePopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withType.UserRecipeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRecipeServiceTest {

	private static final int USER_ID = 1;

	private static final int RECIPE_ID = 100;

	private static final TypeOfInteraction TYPE = TypeOfInteraction.SAVE;

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

	private UserRecipeServiceImpl userRecipeService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	private User user;

	private Recipe recipe;

	private UserRecipe userRecipe;

	@BeforeEach
	void setUp() {
		userRecipeService = new UserRecipeServiceImpl(userRecipeRepository, recipeRepository, userRepository,
				recipeMapper, recipePopulationService);
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		recipe = new Recipe();
		recipe.setId(RECIPE_ID);
		recipe.setIsPublic(true);
		userRecipe = UserRecipe.of(user, recipe, TYPE);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(recipe));

		userRecipeService.saveToUser(RECIPE_ID, TYPE);

		verify(userRecipeRepository).save(any(UserRecipe.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotSupportedInteractionTypeExceptionIfRecipeIsPrivate() {
		Recipe privateRecipe = new Recipe();
		privateRecipe.setId(RECIPE_ID);
		privateRecipe.setIsPublic(false);

		when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(privateRecipe));

		assertThrows(NotSupportedInteractionTypeException.class, () -> userRecipeService.saveToUser(RECIPE_ID, TYPE));

		verify(userRecipeRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userRecipeService.saveToUser(RECIPE_ID, TYPE));

		verify(userRecipeRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userRecipeService.saveToUser(RECIPE_ID, TYPE));

		verify(userRecipeRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfRecipeNotFound() {
		when(userRecipeRepository.existsByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userRecipeService.saveToUser(RECIPE_ID, TYPE));

		verify(userRecipeRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		when(userRecipeRepository.findByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE))
			.thenReturn(Optional.of(userRecipe));

		userRecipeService.deleteFromUser(RECIPE_ID, TYPE);

		verify(userRecipeRepository).delete(userRecipe);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserRecipeNotFound() {
		when(userRecipeRepository.findByUserIdAndRecipeIdAndType(USER_ID, RECIPE_ID, TYPE))
			.thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userRecipeService.deleteFromUser(RECIPE_ID, TYPE));

		verify(userRecipeRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllRecipesByType() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Recipe recipe2 = new Recipe();
		recipe2.setId(2);
		UserRecipe ur2 = UserRecipe.of(user, recipe2, TYPE);

		RecipeSummaryDto dto1 = new RecipeSummaryDto();
		dto1.setId(RECIPE_ID);
		RecipeSummaryDto dto2 = new RecipeSummaryDto();
		dto2.setId(2);

		Page<UserRecipe> userRecipePage = new PageImpl<>(List.of(userRecipe, ur2), pageable, 2);

		when(userRecipeRepository.findAllByUserIdAndType(eq(USER_ID), eq(TYPE), eq(pageable)))
			.thenReturn(userRecipePage);
		when(recipeRepository.findByIdsWithDetails(any())).thenReturn(List.of(recipe, recipe2));
		when(recipeMapper.toSummary(recipe)).thenReturn(dto1);
		when(recipeMapper.toSummary(recipe2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userRecipeService.getAllFromUser(USER_ID, TYPE, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(userRecipeRepository).findAllByUserIdAndType(eq(USER_ID), eq(TYPE), eq(pageable));
		verify(recipePopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoRecipes() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<UserRecipe> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userRecipeRepository.findAllByUserIdAndType(eq(USER_ID), eq(TYPE), eq(pageable))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userRecipeService.getAllFromUser(USER_ID, TYPE, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
