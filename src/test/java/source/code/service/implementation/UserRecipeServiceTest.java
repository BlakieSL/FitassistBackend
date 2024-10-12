package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserRecipeServiceTest {
  @Mock
  private UserRecipeRepository userRecipeRepository;
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  UserRecipeServiceImpl userRecipeService;
  @BeforeEach
  void setup() {

  }

  @Test
  void saveRecipeToUser_shouldSave_whenNotAlreadySavedAndUserAndRecipeFound() {

  }

  @Test
  void saveRecipeToUser_shouldThrowException_whenIsAlreadySaved() {

  }

  @Test
  void saveRecipeToUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void saveRecipeToUser_shouldThrowException_whenRecipeNotFound() {

  }

  @Test
  void deleteSavedRecipeFromUser_shouldDelete_whenUserRecipeFound() {

  }

  @Test
  void deleteSavedRecipeFromUser_shouldThrowException_whenUserRecipeNotFound() {

  }

  @Test
  void calculateRecipeLikesAndSaves_shouldCalculate_whenRecipeFound() {

  }

  @Test
  void calculateRecipeLikesAndSaves_shouldThrowException_whenRecipeNotFound() {

  }

  @Test
  void calculateRecipeLikedAndSaves_shouldReturnZeros_whenNoLikesAndSaves() {

  }

}

