package source.code.service.declaration;

import source.code.dto.response.LikesAndSavesResponseDto;

public interface UserRecipeService {
    void saveRecipeToUser(int recipeId, int userId, short type);
    void deleteSavedRecipeFromUser(int recipeId, int userId, short type);
    LikesAndSavesResponseDto calculateRecipeLikesAndSaves(int recipeId);
}