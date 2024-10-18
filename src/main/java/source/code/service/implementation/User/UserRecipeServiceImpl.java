package source.code.service.implementation.User;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.mapper.Recipe.RecipeMapper;
import source.code.model.Recipe.Recipe;
import source.code.model.User.User;
import source.code.model.User.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.UserRecipeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserRecipeServiceImpl implements UserRecipeService {
  private final UserRecipeRepository userRecipeRepository;
  private final RecipeRepository recipeRepository;
  private final UserRepository userRepository;
  private final RecipeMapper recipeMapper;

  public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository,
                               RecipeRepository recipeRepository,
                               UserRepository userRepository,
                               RecipeMapper recipeMapper) {
    this.userRecipeRepository = userRecipeRepository;
    this.recipeRepository = recipeRepository;
    this.userRepository = userRepository;
    this.recipeMapper = recipeMapper;
  }

  @Transactional
  public void saveRecipeToUser(int userId, int recipeId, short type) {
    if (isAlreadySaved(userId, recipeId, type)) {
      throw new NotUniqueRecordException(
              "User with id: " + userId
                      + " already has recipe with id: " + recipeId
                      + " and type: " + type);
    }

    User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));

    Recipe recipe = recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Recipe with id: " + recipeId + " not found"));

    UserRecipe userRecipe =
            UserRecipe.createWithUserRecipeType(user, recipe, type);
    userRecipeRepository.save(userRecipe);
  }

  @Transactional
  public void deleteSavedRecipeFromUser(int recipeId, int userId, short type) {
    UserRecipe userRecipe = userRecipeRepository
            .findByUserIdAndRecipeIdAndType(userId, recipeId, type)
            .orElseThrow(() -> new NoSuchElementException(
                    "UserRecipe with user id: " + userId
                            + ", recipe id: " + recipeId
                            + " and type: " + type + " not found"));

    userRecipeRepository.delete(userRecipe);
  }

  public List<RecipeResponseDto> getRecipesByUserAndType(int userId, short type) {
    List<UserRecipe> userRecipes = userRecipeRepository.findByUserIdAndType(userId, type);

    List<Recipe> recipes = userRecipes.stream()
            .map(UserRecipe::getRecipe)
            .collect(Collectors.toList());

    return recipes.stream()
            .map(recipeMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public LikesAndSavesResponseDto calculateRecipeLikesAndSaves(int recipeId) {
    recipeRepository.findById(recipeId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Recipe with id: " + recipeId + " not found"));

    long saves = userRecipeRepository.countByRecipeIdAndType(recipeId, (short) 1);
    long likes = userRecipeRepository.countByRecipeIdAndType(recipeId, (short) 2);

    return new LikesAndSavesResponseDto(likes, saves);
  }

  private boolean isAlreadySaved(int userId, int recipeId, short type) {
    return userRecipeRepository.existsByUserIdAndRecipeIdAndType(userId, recipeId, type);
  }

}