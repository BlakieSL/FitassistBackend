package source.code.controller.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.service.declaration.User.UserRecipeService;

import java.util.List;

public class UserRecipeController {
  private final UserRecipeService userRecipeService;

  public UserRecipeController(UserRecipeService userRecipeService) {
    this.userRecipeService = userRecipeService;
  }


  @GetMapping("/users/{userId}/type/{type}")
  public ResponseEntity<List<RecipeResponseDto>> getRecipesByUserAndType(@PathVariable int userId,
                                                                         @PathVariable short type) {
    List<RecipeResponseDto> recipes = userRecipeService.getRecipesByUserAndType(userId, type);
    return ResponseEntity.ok(recipes);
  }

  @GetMapping("/recipes/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getRecipeLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userRecipeService.calculateRecipeLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/users/{userId}/recipes/{recipeId}/type/{typeId}")
  public ResponseEntity<Void> saveRecipeToUser(
          @PathVariable int userId, @PathVariable int recipeId, @PathVariable short typeId) {

    userRecipeService.saveRecipeToUser(userId, recipeId, typeId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/{userId}/recipes/{recipeId}/type/{typeId}")
  public ResponseEntity<Void> deleteSavedRecipeFromUser(
          @PathVariable int userId, @PathVariable int recipeId, @PathVariable short typeId) {

    userRecipeService.deleteSavedRecipeFromUser(recipeId, userId, typeId);
    return ResponseEntity.ok().build();
  }
}
