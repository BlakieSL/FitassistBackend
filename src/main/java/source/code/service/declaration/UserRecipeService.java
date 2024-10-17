package source.code.service.declaration;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.RecipeResponseDto;

import java.util.List;

public interface UserRecipeService {
  void saveRecipeToUser(int recipeId, int userId, short type);

  void deleteSavedRecipeFromUser(int recipeId, int userId, short type);

  List<RecipeResponseDto> getRecipesByUserAndType(int userId, short type);

  LikesAndSavesResponseDto calculateRecipeLikesAndSaves(int recipeId);
}