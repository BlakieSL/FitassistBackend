package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.mapper.RecipeMapper;
import source.code.repository.RecipeCategoryAssociationRepository;
import source.code.repository.RecipeCategoryRepository;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.service.implementation.Recipe.RecipeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
  @Mock
  private RecipeMapper recipeMapper;
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private UserRecipeRepository userRecipeRepository;
  @Mock
  private RecipeCategoryRepository recipeCategoryRepository;
  @Mock
  private RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;
  @InjectMocks
  private RecipeServiceImpl recipeService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createRecipe_shouldCreate() {

  }

  @Test
  void getRecipe_shouldReturnRecipe_whenRecipeFound() {

  }

  @Test
  void getRecipe_shouldThrowException_whenRecipeNotFound() {

  }

  @Test
  void getAllRecipes_shouldReturnRecipes_whenRecipesFound() {

  }

  @Test
  void getAllRecipes_shouldReturnEmptyList_whenNoRecipesFound() {

  }

  @Test
  void getRecipesByUser_shouldReturnRecipes_whenRecipesFound() {

  }

  @Test
  void getRecipesByUser_shouldReturnEmptyList_whenNoRecipesFound() {

  }

  @Test
  void getAllCategories_shouldReturnCategories_whenCategoriesFound() {

  }

  @Test
  void getAllCategories_shouldReturnEmptyList_whenNoCategoriesFound() {

  }

  @Test
  void getRecipesByCategory_shouldReturnRecipes_whenRecipesFound() {

  }

  @Test
  void getRecipesByCategory_shouldReturnEmptyList_whenNoRecipesFound() {

  }
}
