package source.code.service.implementation.User;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.Recipe.RecipeMapper;
import source.code.model.Recipe.Recipe;
import source.code.model.User.User;
import source.code.model.User.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.User.SavedService;
import source.code.service.declaration.User.UserRecipeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service("userRecipeService")
public class UserRecipeServiceImpl
        extends GenericSavedService<Recipe, UserRecipe, RecipeResponseDto>
        implements SavedService {


  public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository,
                               RecipeRepository recipeRepository,
                               UserRepository userRepository,
                               RecipeMapper recipeMapper) {
    super(userRepository, recipeRepository, userRecipeRepository, recipeMapper::toResponseDto);
  }


  @Override
  protected boolean isAlreadySaved(int userId, int entityId, short type) {
    return ((UserRecipeRepository) userEntityRepository)
            .existsByUserIdAndRecipeIdAndType(userId, entityId,type);
  }

  @Override
  protected UserRecipe createUserEntity(User user, Recipe entity, short type) {
    return UserRecipe.createWithUserRecipeType(user, entity, type);
  }

  @Override
  protected UserRecipe findUserEntity(int userId, int recipeId, short type) {
    return ((UserRecipeRepository) userEntityRepository)
            .findByUserIdAndRecipeIdAndType(userId, recipeId, type)
            .orElseThrow(() -> new RecordNotFoundException("UserRecipe", userId, recipeId, type));
  }

  @Override
  protected List<UserRecipe> findAllByUserAndType(int userId, short type) {
    return ((UserRecipeRepository) userEntityRepository).findByUserIdAndType(userId, type);
  }

  @Override
  protected Recipe extractEntity(UserRecipe userRecipe) {
    return userRecipe.getRecipe();
  }

  @Override
  protected long countSaves(int recipeId) {
    return ((UserRecipeRepository) userEntityRepository).countByRecipeIdAndType(recipeId, (short) 1);
  }

  @Override
  protected long countLikes(int recipeId) {
    return ((UserRecipeRepository) userEntityRepository).countByRecipeIdAndType(recipeId, (short) 2);
  }
}